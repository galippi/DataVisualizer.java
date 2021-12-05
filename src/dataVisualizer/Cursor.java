package dataVisualizer;

public class Cursor {
    Cursor(DataPanel _parent)
    {
        parent = _parent;
    }

    DataPanel parent;
    int hPos = -1;
}
