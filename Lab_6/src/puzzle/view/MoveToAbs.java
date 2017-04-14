package puzzle.view;

import javafx.scene.Node;
import javafx.scene.shape.MoveTo;

/**
 * Created by M4teo on 14.04.2017.
 */
class MoveToAbs extends MoveTo
{
    public MoveToAbs(Node node)
    {
        super(node.getLayoutBounds().getWidth()/2 , node.getLayoutBounds().getHeight()/2 );
    }
}
