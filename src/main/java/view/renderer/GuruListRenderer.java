package view.renderer;

import model.Guru;
import model.User;

import javax.swing.*;
import java.awt.*;

public class GuruListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof Guru) {
            setText(((Guru)value).getNamaLengkap());
        } else if (value instanceof User) {
            setText(((User)value).getNamaLengkap());
        }
        return this;
    }
}