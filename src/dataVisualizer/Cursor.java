package dataVisualizer;

public class Cursor {
    Cursor(DataPanel _parent)
    {
        parent = _parent;
    }

    DataPanel parent;
    double hPos = -1;
    int xPos = -9999;
}
