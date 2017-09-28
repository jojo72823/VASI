package com.daumont.vasi.vasi.deezer;

import com.daumont.vasi.vasi.modele.Album;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Créé par JONATHAN DAUMONT le 01/06/2017.
 */

public class List_Album {


    @SerializedName("data")
    @Expose
    private List<Album> data = null;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<Album> getData() {
        return data;
    }

    public void setData(List<Album> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }


}
