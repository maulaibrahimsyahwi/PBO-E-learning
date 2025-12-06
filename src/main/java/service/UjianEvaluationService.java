package service;

import model.Soal;
import javax.swing.ButtonGroup;
import javax.swing.JTextArea;
import java.util.List;

public class UjianEvaluationService {

    public static int hitungNilai(List<Soal> soal, List<Object> inputs) {
        int benar = 0;
        int totalSoal = soal.size();
        
        for (int i = 0; i < totalSoal; i++) {
            Soal s = soal.get(i);
            Object comp = inputs.get(i);
            
            if ("ESSAY".equals(s.getTipeSoal())) {
                if (comp instanceof JTextArea area) {
                    if(!area.getText().isBlank()) {
                       if(area.getText().trim().equalsIgnoreCase(s.getKunciJawaban())) {
                           benar++;
                       }
                    }
                }
            } else {
                if (comp instanceof ButtonGroup bg) {
                    if (bg.getSelection() != null && bg.getSelection().getActionCommand().equals(s.getKunciJawaban())) {
                        benar++;
                    }
                }
            }
        }
        if (totalSoal == 0) return 0;
        return (benar * 100) / totalSoal;
    }
}