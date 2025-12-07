package service;

import model.Soal;
import javax.swing.ButtonGroup;
import java.util.List;

public class UjianEvaluationService {

    public static int hitungNilai(List<Soal> soal, List<Object> inputs) {
        double totalBenar = 0;
        int totalSoal = soal.size();
        
        for (int i = 0; i < totalSoal; i++) {
            Soal s = soal.get(i);
            Object comp = inputs.get(i);
            
            if ("PG".equals(s.getTipeSoal())) {
                if (comp instanceof ButtonGroup bg) {
                    if (bg.getSelection() != null && bg.getSelection().getActionCommand().equals(s.getKunciJawaban())) {
                        totalBenar++;
                    }
                }
            } 
        }
        
        if (totalSoal == 0) return 0;
        return (int) ((totalBenar / totalSoal) * 100);
    }
}