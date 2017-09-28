package com.daumont.vasi.vasi.deezer;

/**
 * Créé par JONATHAN DAUMONT le 01/06/2017.
 */


import com.daumont.vasi.vasi.modele.Artist;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class List_Artist {

    @SerializedName("data")
    @Expose
    private List<Artist> data = null;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("next")
    @Expose
    private String next;

    public List<Artist> getData() {
        return data;
    }

    public void setData(List<Artist> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

}