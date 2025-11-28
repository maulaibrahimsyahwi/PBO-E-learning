package service;

import model.*;
import java.util.*;

public class NilaiService {

    private List<Nilai> nilaiList = new ArrayList<>();

    public void beriNilai(Nilai n) {
        nilaiList.add(n);
        System.out.println("Nilai berhasil diberikan.\n");
    }

    public List<Nilai> getNilai() {
        return nilaiList;
    }
}
