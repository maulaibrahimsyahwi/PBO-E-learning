package service;

import model.Tugas;
import java.time.LocalDate;
import java.util.*;

public class TugasService {

    private List<Tugas> tugasList = new ArrayList<>();

    public void tambahTugas(Tugas t) {
        tugasList.add(t);
        System.out.println("Tugas ditambahkan.\n");
    }

    public List<Tugas> getTugas() {
        return tugasList;
    }

    public boolean sebelumDeadline(Tugas t) {
        return LocalDate.now().isBefore(t.getDeadline());
    }
}
