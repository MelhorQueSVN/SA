import robocode.*;
import standardOdometer.Odometer;
import robocode.util.Utils;
import java.awt.Color;
import java.math.*;

import static java.lang.Thread.sleep;

public class MyRobot extends AdvancedRobot
{
    // Private Instance Variable
    private double distanciaPercorrida = 0.0;
    private double antX;
    private double antY;
    private double cordX;
    private double cordY;
    private boolean posInicial = false;
    private boolean pronto = false;
    private int encontrados = 0;
    private Odometer odometer = new Odometer("OD", this);

    public void run() {
        // Cores do robo
        setColors(Color.orange,Color.red,Color.yellow);
        // inicialização das coordenadas
        this.antX=getX();
        this.antY=getY();
        this.cordX=getX();
        this.cordY=getY();
        // custom events
        addCustomEvent(odometer);
        addCustomEvent(new Condition("timer") {
            public boolean test() {
                //System.out.println(getTime());
                return (getTime()>0);
            }
        });
        
        for(int i=0;i<100;i++)
        	doNothing();
        
        // Virar robo para a origem
        turnRight(225-getHeading());
        // Ir para a posição de origem
        while(!posInicial) {
            move(18, 18);
        }
        // Virar robo de novo 
        turnRight(360-getHeading());
        // está pronto para começar o trajeto
        this.pronto = true;
        // main loop
        while(true){
            turnRight(1); // vai andado 1 grau até visualizar um robot
            if(this.encontrados==3) // já visualizou os 3 robots
                move(18,18);	// volta para origem
            // Prints finais
            if(this.cordX==18.0 && this.cordY==18.0 && pronto && this.encontrados==3){ 
            	System.out.println("************************* Stats *************************"); 
            	double rounded = Math.floor(this.distanciaPercorrida * 100) / 100;
                System.out.println("Distancia percorrida: " + rounded + " pixels");
                System.out.println("Distance medida pelo odometro dado: " + this.odometer.getRaceDistance());   
            	System.out.println("*********************************************************");
                pronto=false;
            }
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        if(pronto){
            turnLeft(e.getBearing()*1.7);
            ahead(e.getDistance()+13);
            setTurnRight(57);
            ahead(45);
            this.encontrados++;
        }
    }

    /**
     * onHitRobot: What to do when you see another robot
     */
    public void onHitRobot(HitRobotEvent e){
        setTurnLeft(20);
        back(20);
    }
	
    /**
     * onCustomEvent handler
     */
    public void onCustomEvent(CustomEvent e) {
        // se o timer foi invocado,
        if(e.getCondition().getName().equals("timer")) {
            this.cordX = getX();
            this.cordY = getY();
            if(this.cordX==18.0 && this.cordY==18.0 && this.encontrados==0)
                posInicial=true;
            if(pronto){
            	atualizaDist(this.cordX,this.cordY,this.antX, this.antY);    
            }
            // atualiza os valores das coordenadas antigas
            this.antX = getX();
            this.antY = getY();
        }
    }
    
    // Função auxiliar para atualizar a distância total percorrida
    private void atualizaDist(double cordx, double cordy, double antx, double anty) { 
    	double dist = Math.sqrt(Math.pow((cordx -antx),2) + Math.pow((cordy - anty), 2));
    	this.distanciaPercorrida += dist;
    }

    private void move(double x, double y) { 
    	 /* Calculate the difference bettwen the current position and the target position. */
        x = x - getX();
        y = y - getY();
     
        /* Calculate the angle relative to the current heading. */
        double goAngle = Utils.normalRelativeAngle(Math.atan2(x, y) - getHeadingRadians());
     
        /*
         * Apply a tangent to the turn this is a cheap way of achieving back to front turn angle as tangents period is PI.
         * The output is very close to doing it correctly under most inputs. Applying the arctan will reverse the function
         * back into a normal value, correcting the value. The arctan is not needed if code size is required, the error from
         * tangent evening out over multiple turns.
         */
        setTurnRightRadians(Math.atan(Math.tan(goAngle)));
     
        /* 
         * The cosine call reduces the amount moved more the more perpendicular it is to the desired angle of travel. The
         * hypot is a quick way of calculating the distance to move as it calculates the length of the given coordinates
         * from 0.
         */
        ahead(Math.cos(goAngle) * Math.hypot(x, y));
    }
}