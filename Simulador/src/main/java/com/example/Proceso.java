package com.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Proceso {

    private int tr = 0;
    private int tejec = 0;
    private int te = 0;
    private int idp;
    private int ta;
    private int ti;
    private int tam;

    public void aumentarTE(){
        this.te += 1;
    }

    public void aumentarTejec(){
        this.tejec += 1;
    }
}