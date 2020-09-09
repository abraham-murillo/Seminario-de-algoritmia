package necesitoAyuda;

import javax.swing.*;
import java.util.ArrayList;

public class Hilox implements Runnable{
    boolean detener = false;
    Thread t;
    int tiempoDeEspera;
    JLayeredPane panelPorCapasImagenFondo;
    int movimientos;
    ArrayList<Agente> agentes;
    Senuelo senuelo;

    public Hilox(JLayeredPane panel, ArrayList<Agente> agentes, Senuelo senuelo, int espera, int movimientos){
        this.panelPorCapasImagenFondo = panel;
        this.agentes = agentes;
        this.senuelo = senuelo;
        this.movimientos = movimientos;
        this.tiempoDeEspera = (espera * 10);
        t = new Thread(this, "Debe de jalar");
        inicia();
    }

    public void run(){
        try{
            for(int i = 0; i < movimientos; i++){
                // System.out.println(Thread.currentThread().getName() + ": " + i);
                panelPorCapasImagenFondo.add(senuelo, 0);
                for(Agente agente: agentes){
                    agente.avanza();
                    panelPorCapasImagenFondo.add(agente, 0);
                }
                Thread.currentThread().sleep(tiempoDeEspera);

                synchronized(this){
                    while(detener == true){
                        wait();
                    }
                }
            }
        }catch(Exception e){

        }
    }

    synchronized void inicia(){
        t.start();
        System.out.println("Inicia");
    }

    synchronized void detente(){
        detener = true;
        System.out.println("Pausa");
    }
    synchronized void continuar(){
        detener = false;
        notify();
        System.out.println("Continuar");
    }
}
