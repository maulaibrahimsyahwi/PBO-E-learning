package service;

import model.Ujian;
import java.util.*;

public class UjianService {

    private List<Ujian> ujianList = new ArrayList<>();

    public void tambahUjian(Ujian u) {
        ujianList.add(u);
        System.out.println("Ujian ditambahkan.\n");
    }

    public List<Ujian> getUjian() {
        return ujianList;
    }
}
