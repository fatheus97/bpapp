package dbModel;

import java.util.List;

public interface Showable {
    String getHeader();
    String[] getColumnNames();
    String[][] getContent();
}
