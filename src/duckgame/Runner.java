package duckgame;

/**
 * Write a description of class Runner here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.io.FileNotFoundException;
import java.util.*;
import org.apache.commons.math3.geometry.euclidean.threed.*;

public class Runner
{
    private Gun gun;
    private Shot shot;
    private Duck duck;
    private Game game;
    private long timeOfMinimumDistance;
    private double criticalDistance = 2;
    boolean duckFalls;
    private int tempNMinDistCalls = 0;
    private long duckCreatedTime = 0;
    
    Runner(String gunType, int gauge, String choke, double shotSize, String shotMaterial, int difficulty) throws FileNotFoundException
    {
        shot = new Shot(0, 0.0, "");
        gun = new Gun(0, "", shot);
        timeOfMinimumDistance = 0;
        duckFalls = false;
    }
    
    public static void main(String args[])
    {
        //Tells gui to display a menu with selections for a gun and ammunition)
        //recieves gun params from gui
        //recieves Shot params from gui
        //calculates nOfPellets, weight overall, surface area of each pellet (for wind resistance)
        //displays x-y view of game screen, with a button for exiting back to the menu. //will eventually be x-y-z
        //don't draw the gun yet // will eventually draw a rectangle
        //draw crosshairs instead of a mouse pointer
        //generate a duck with a random position vector within u units of the gun, and a random velocity vector, generate a new duck at random times between every 5-20 seconds. 
        //set the duck's z-velocity to zero // will eventually omit this
        //wait for a mouseClicked event to fire.
        //when the gun fires, depending on angle of the pointer, and the mass of each pellet, caluclate the initial shot velocity, and start a timer
        //find the distance to the duck,
        //set the shot's z-velocity to zero //will eventually omit this.
        //every n nanoseconds, account for every force acting on the shot/duck and recalculate their locations. Repaint the board.
        //until all locations in the shot have travelled d units past the distnace to the duck, record all positions of the duck and of the shot, sorted by time since the shot.
        //reset the timer
        //determine if there are any common points at the same time
        //determine whether or not the duck will fall using probablility and the common points.
        //repeat the process.
        
        //********************************************************DO THIS IN THE GUI!
        ////Call The GUI stuff*************************************^^^^^^^^^^^^^^
        ////wait for input from the gui ***************************^^^^^^^^^^^^^^
        //shot = new Shot(gauge, shotSize, shotMaterial);//******^^^^^^^^^^^^^^
        //gun = new Gun(gauge, gunType, shot);//***************^^^^^^^^^^^^^^
        ////wait for start popup to be okayed*********************^^^^^^^^^^^^^^
        //*******************************************************^^^^^^^^^^^^^^
        //wait for input from the gui to fire
    }
    
    public void fireGun(double xAim, double zAim) //only fire if gun.canShoot() 
    {
            gun.shoot(xAim, zAim);
            long nowTime = System.nanoTime();
            duck.setReferancePoint(duck.calcReferancePoint(nowTime - duckCreatedTime)); // Need to recalc new duck ref pt at shoot time.
            System.out.println("New Duck Ref Pt = " + duck.getReferancePoint());
            timeOfMinimumDistance = minimumDistanceTime(0L, 10000000000L); //10 seconds in ns
            setDuckFalls(duckIsHit());
            if (duckFalls)
            {
                //duck.setFallTime(timeOfMinimumDistance);
                duck.setFallTime(System.nanoTime());
            }
    }
    
    public void makeDucks()
    {
         //DuckCreator duckcreator = new DuckCreator();
        setDuck(new Duck(game.getWindowWidth(), game.getWindowHeight())); //this is a one-time duck creation. fix this later.
        duckCreatedTime = System.nanoTime();
    }
    
    public long getDuckCreatedTime()
    {
        return duckCreatedTime;
    }
    
    public void setDuckFalls(boolean df)
    {
         duckFalls = df;
    }
    
    public boolean getDuckFalls()
    {
        return duckFalls;
    }
    
    private long minimumDistanceTime(long timeStart, long timeEnd) // in nanoseconds
    {
        tempNMinDistCalls++;
        System.out.println("StartTime = " + timeStart + ", EndTime = " + timeEnd);
        
        ArrayList<Double> distances= new ArrayList<Double>();
        long timeSegment = timeEnd-timeStart;
        double d1 = shot.calcReferancePoint((timeSegment/1000) + timeStart).distance(duck.calcReferancePoint(timeSegment/1000));
        double d2 = shot.calcReferancePoint((timeSegment/10) + timeStart).distance(duck.calcReferancePoint(timeSegment/10));
        double d3 = shot.calcReferancePoint((timeSegment/5) + timeStart).distance(duck.calcReferancePoint(timeSegment/5));
        double d4 = shot.calcReferancePoint((timeSegment/2) + timeStart).distance(duck.calcReferancePoint(timeSegment/2));
        double d5 = shot.calcReferancePoint((timeSegment/1) + timeStart).distance(duck.calcReferancePoint(timeSegment/1));
        
        distances.add(d1);
        distances.add(d2);
        distances.add(d3);
        distances.add(d4);
        distances.add(d5);
        
        double min = d1;
        int ind = 0;
        for (double d : distances)
        {
            if (d < min)
            {
                min = d;
                ind = distances.indexOf(d);
                
            }
            System.out.println("Distance = " + d);
        }
        
        System.out.println("Min Distance = " + min + ", Min Index = " + ind);
        
        if (timeSegment > 200000) // 1/5ms = 200000ns. @ a spd of 1000ft/s, will travel 1/5th of a foot in 200000ns.  
        {
            if (ind == 0)
            {
                return minimumDistanceTime(timeStart, timeStart+timeSegment/10L);
            }
            else if (ind == 1)
            {
                return minimumDistanceTime((timeStart+timeSegment/1000L), (timeStart+timeSegment/5L));
            }
            else if (ind == 2)
            {
                return minimumDistanceTime((timeStart+timeSegment/10L), (timeStart+timeSegment/2L));
            }
            else if (ind == 3)
            {
                return minimumDistanceTime((timeStart+timeSegment/5L), timeEnd);
            }
            else if (ind == 4)
            {
                return minimumDistanceTime((timeStart+timeSegment/2L), timeEnd);
            }
        }
        
        System.out.println("tempNMinDistCalls = " + tempNMinDistCalls);
        return timeStart;
    }
    public boolean duckIsHit()
    {
        ArrayList<Vector3D> duckLocations = duck.calcLocations(timeOfMinimumDistance);
        ArrayList<Vector3D> shotLocations = shot.calcLocations(timeOfMinimumDistance);
        Vector3D shotRefPt = shot.calcReferancePoint(timeOfMinimumDistance);
        int nCubesInside = 0;
        
        for (Vector3D duckL : duckLocations)
        {
            int ctr = 0;
            for (Vector3D shotL : shotLocations)
            {
                if (duckL.distance(shotRefPt) < shotL.distance(shotRefPt))
                {
                    ctr++;
                }
                if (ctr > 0)
                {
                    nCubesInside++;
                }
            }
        }
        
        return nCubesInside > 0;
    }
    public void setDuck(Duck duck)
    {
        this.duck=duck;
    }
    public Duck getDuck()
    {
        return duck;
    }
    public void setGame (Game game)
    {
        this.game = game;
    }
    
//    public class DuckCreator
//    {
//        private Timer timer;
//    
//        public DuckCreator()
//        {
//            timer = new Timer();
//            timer.schedule(new DuckMaker(), (int)(Math.random()*4999+1));
//        }
//        
//        public void setDuckDC(Duck duck)
//        {
//            setDuck(duck);
//        }
//        public void setTimer(Timer newTimer)
//        {
//            timer = newTimer;
//        }
//        public Timer getTimer()
//        {
//            return timer;
//        }
//        private class DuckMaker extends TimerTask
//        {  
//            @Override
//            public void run() 
//            {
//                setDuckDC(new Duck(game.getWindowWidth(), game.getWindowHeight())); 
//                timer.cancel();
//                timer = new Timer();
//                while (game.getWindowHeight() + 1000 > Math.abs(game.getDuckYPos()) && 
//                       game.getWindowWidth() + 1000 > Math.abs(game.getDuckYPos()))  //wouldn't want an infinite loop here
//                {
//                    if (game.getWindowHeight() < game.getDuckYPos() || game.getDuckYPos() < 0 ||
//                         game.getWindowWidth() < game.getDuckXPos() || game.getDuckXPos() < 0)
//                    {
//                        timer.schedule(new DuckMaker(), (int)(Math.random()*4999+1));
//                    }
//                    else
//                    {
//                        try
//                        {
//                            Thread.sleep(250);
//                        }
//                        catch (InterruptedException ex)
//                        {
//                            //Ignore
//                        }
//                    }
//                }
//                
//            }
//        }
//    }
}
