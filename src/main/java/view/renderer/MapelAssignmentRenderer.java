package view.renderer;

import model.MataPelajaran;

import javax.swing.*;
import java.awt.*;

public class MapelAssignmentRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof MataPelajaran) {
            MataPelajaran mp = (MataPelajaran) value;
            setText(mp.getNamaMapel() + " (Tingkat " + mp.getTingkat() + ")");
        }
        return this;
    }
}