package view.renderer;

import model.Kelas;

import javax.swing.*;
import java.awt.*;

public class KelasListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof Kelas) {
            setText(((Kelas)value).getNamaKelas());
        }
        return this;
    }
}