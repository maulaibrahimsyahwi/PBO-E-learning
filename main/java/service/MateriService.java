package service;

import model.Materi;
import java.util.*;

public class MateriService {

    private List<Materi> materiList = new ArrayList<>();

    public void tambahMateri(Materi m) {
        materiList.add(m);
        System.out.println("Materi diunggah.\n");
    }

    public List<Materi> getMateri() {
        return materiList;
    }
}
