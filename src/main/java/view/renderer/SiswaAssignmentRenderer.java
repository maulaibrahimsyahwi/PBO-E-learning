package view.renderer;

import model.Siswa;

import javax.swing.*;
import java.awt.*;

public class SiswaAssignmentRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if(value instanceof Siswa) {
            Siswa s = (Siswa) value;
            String kelasStr = (s.getKelas() != null) ? s.getKelas().getNamaKelas() : "-";
            setText(s.getNamaLengkap() + " (" + s.getNis() + ") - Kelas: " + kelasStr);
        }
        return this;
    }
}