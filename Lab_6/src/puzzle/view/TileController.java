package puzzle.view;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.scene.shape.*;
import javafx.animation.PathTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import puzzle.Main;
import javafx.scene.control.Label;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.io.File;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.layout.TilePane;
import java.util.Collections;
import javafx.scene.Node;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import puzzle.model.Tile;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.PrintWriter;
import java.util.Scanner;



public class TileController  {

    @FXML
    private Main main;
    @FXML
    private TilePane panel;
    @FXML
    private Label timeLabel;

    @FXML private Label bestTimeLabel;

    ArrayList<Tile> tilesList = new ArrayList<Tile>();

    @FXML
    Tile first;
    @FXML
    Tile second;

    private BufferedImage[] parts = new BufferedImage[9];
    private Tile[] tiles = new Tile[9];
    private Timeline aTimeline;
    private String timeFormat;
    private File aFile;
    private Scanner aScanner;
    private long seconds;
    private long minute;
    private long millis;
    private long bestTime;

    private long time = 0;
    private void updateTime()
    {
        seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        minute = TimeUnit.MILLISECONDS.toMinutes(time);
        millis = time - TimeUnit.SECONDS.toMillis(seconds);
        timeFormat = String.format("%02d:%02d:%d", minute, seconds, millis);
        timeLabel.setText(timeFormat);
        time += 100;
    }

    private void loadFile()
    {
        try{
            aFile = new File("src/assets/bestTime.txt");
            aScanner = new Scanner(aFile);
            long tmpValue = 0;
            String format = new String();
            String tmp = aScanner.nextLine();
            format = tmp + ":";
            tmpValue += Long.parseLong(tmp);
            tmpValue *= 10000;
            bestTime += tmpValue;
            tmp = aScanner.nextLine();
            format +=tmp + ":";
            tmpValue = Long.parseLong(tmp);
            tmpValue *= 1000;
            bestTime += tmpValue;
            tmp = aScanner.nextLine();
            format +=tmp;
            tmpValue = Long.parseLong(tmp);
            bestTime += tmpValue;
            bestTimeLabel.setText(format);
            aScanner.close();

        }catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void saveFile()
    {
        long tmpTime = minute * 10000 + seconds * 1000 + millis;

        if(tmpTime < bestTime)
        try
        {
            PrintWriter write = new PrintWriter("src/assets/bestTime.txt");
            String tmpString = new String(String.valueOf(minute));
            write.println(tmpString);
            tmpString = String.valueOf(seconds);
            write.println(tmpString);
            tmpString = String.valueOf(millis);
            write.println(tmpString);
            write.close();
        }

        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private boolean isWon()
    {
        boolean returnedValue = false;

        for (int i = 0; i < 9; i++)
            if(tilesList.get(i).getid() == i)
                returnedValue = true;

            else
                return false;

        return returnedValue;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetheight)
    {

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetheight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetheight, null);
        g.dispose();

        return resizedImage;
    }

    private PathTransition getPathTransition(Tile first, Tile second)
    {
        PathTransition ptr = new PathTransition();
        Path path = new Path();
        path.getElements().clear();
        path.getElements().add(new MoveToAbs(first));
        path.getElements().add(new LineToAbs(first, second.getLayoutX(), second.getLayoutY()));
        ptr.setPath(path); ptr.setNode(first);
        return ptr;
    }

    @FXML
    private void initialize(){

        try
        {
            loadFile();
            BufferedImage img = ImageIO.read(new File("out/production/puzzle/assets/waterfall.jpg"));
            BufferedImage Img=resizeImage(img,600,600);

            for(int i = 0, x = 0, y = 0; i < 9; i++, x += 200)
            {
                parts[i] = Img.getSubimage(x,y, 200,200);
                tiles[i] = new Tile(200, 200, parts[i], i);
                tiles[i].setLayoutX(x);
                tiles[i].setLayoutY(y);
                tiles[i].setFill(new ImagePattern(SwingFXUtils.toFXImage(tiles[i].getPart(),null)));
                tilesList.add(tiles[i]);

                if (x == 400)
                {
                    x = -200;
                    y += 200;
                }

            }

            panel.getChildren().addAll(tilesList);
            first = null;
            second = null;

            for (Tile tile: tiles)
            {

                tile.setOnMouseClicked(new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {

                            if (first == null)
                            {
                                first = (Tile) event.getSource();
                                return;
                            }

                            if (first != null)
                                second = (Tile) event.getSource();

                            if (first != null && second != null && first != second)
                            {
                                double fx = first.getLayoutX();
                                double fy = first.getLayoutY();
                                double sx = second.getLayoutX();
                                double sy = second.getLayoutY();

                                Collections.swap(tilesList, first.getid(), second.getid());
                                PathTransition ptr = getPathTransition(first, second);
                                PathTransition ptr2 = getPathTransition(second, first);
                                ParallelTransition pt = new ParallelTransition(ptr, ptr2);
                                pt.play();
                                pt.setOnFinished(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        first.setTranslateX(0);
                                        first.setTranslateY(0);
                                        second.setTranslateX(0);
                                        second.setTranslateY(0);

                                        first.setLayoutX(sx);
                                        first.setLayoutY(sy);
                                        second.setLayoutX(fx);
                                        second.setLayoutY(fy);

                                        first.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesList.get(first.getid()).getPart(),null)));
                                        second.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesList.get(second.getid()).getPart(),null)));
                                        first = null;
                                        second = null;
                                    }
                                });

                                if(isWon())
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (aTimeline != null)
                                                aTimeline.stop();
                                            saveFile();
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Congratulation");
                                            alert.setHeaderText("You Won!");
                                            alert.setContentText("Your time: " + timeLabel.getText());
                                            alert.showAndWait();
                                        }
                                    });

                            }
                    }
                 });
               }

        }catch(IOException e){
            System.out.println("Error reading file!");
        }
    }

    @FXML
    private void handleRunBtnAction(){
        Collections.shuffle(tilesList);
        for (int i = 0; i < tilesList.size(); i++)
        {
            Tile tile = tilesList.get(i);
            int num = tile.getid();
            tile.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesList.get(num).getPart(), null)));
        }
        aTimeline = new Timeline(new KeyFrame(Duration.millis(100),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        updateTime();
                    }
                }));
        aTimeline.setCycleCount(Animation.INDEFINITE);
        aTimeline.play();
    }

    @FXML
    public void setMainApp(Main main) {
        this.main = main;
    }

}

