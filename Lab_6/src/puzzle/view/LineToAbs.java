package puzzle.view;

import javafx.scene.Node;
import javafx.scene.shape.LineTo;

/**
 * Created by M4teo on 14.04.2017.
 */
class LineToAbs extends LineTo
{
    public LineToAbs(Node node, double x, double y)
    {
        super(x - node.getLayoutX() + node.getLayoutBounds().getWidth()/2 , y - node.getLayoutY() + node.getLayoutBounds().getHeight()/2 );
    }
}
