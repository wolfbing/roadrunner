/*
     RoadRunner - an automatic wrapper generation system for Web data sources
     Copyright (C) 2003  Valter Crescenzi - crescenz@dia.uniroma3.it

     This program is  free software;  you can  redistribute it and/or
     modify it  under the terms  of the GNU General Public License as
     published by  the Free Software Foundation;  either version 2 of
     the License, or (at your option) any later version.

     This program is distributed in the hope that it  will be useful,
     but  WITHOUT ANY WARRANTY;  without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     General Public License for more details.

     You should have received a copy of the GNU General Public License
     along with this program; if not, write to the:

     Free Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

     ----

     RoadRunner - un sistema per la generazione automatica di wrapper su sorgenti Web
     Copyright (C) 2003  Valter Crescenzi - crescenz@dia.uniroma3.it

     Questo  programma  software libero;   lecito redistribuirlo  o
     modificarlo secondo i termini della Licenza Pubblica Generica GNU
     come � pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma  distribuito nella speranza che sia  utile, ma
     SENZA  ALCUNA GARANZIA;  senza neppure la  garanzia implicita  di
     NEGOZIABILIT�  o di  APPLICABILIT� PER  UN PARTICOLARE  SCOPO. Si
     veda la Licenza Pubblica Generica GNU per avere maggiori dettagli.

     Questo  programma deve  essere  distribuito assieme  ad una copia
     della Licenza Pubblica Generica GNU; in caso contrario, se ne pu
     ottenere  una scrivendo  alla:

     Free  Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

*/
/*
 * Box.java
 *
 * Created on 5 dicembre 2003, 16.49
 * @author  Luigi Arlotta, Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import java.util.*;
import java.util.logging.*;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

public class Box extends Rectangle2D.Double  implements Comparable {
    
    static private Logger log = Logger.getLogger(Box.class.getName());
    
    final public static String BBMARK    = "BB:";
    final public static String SEP       = ",";
    
    public static boolean isBoundingBoxEncoding(String candidate) {
        return (candidate.startsWith(BBMARK));
    }
    
    public static Box decode(String encode) {
        try {
            String coordinates = encode.substring(BBMARK.length());
            StringTokenizer tokenizer = new StringTokenizer(coordinates,SEP);
            int minx = Integer.parseInt(tokenizer.nextToken());
            int miny = Integer.parseInt(tokenizer.nextToken());
            int maxx = Integer.parseInt(tokenizer.nextToken());
            int maxy = Integer.parseInt(tokenizer.nextToken());
            return new Box(minx,miny,maxx,maxy);
        }
        catch (NumberFormatException nfe) {
            log.severe("Cannot decode bb from given string" +encode);
            System.exit(-1);
            return null;
        }
    }
    
    public static String encode(Box b) {
        return BBMARK+(int)b.getMinX()+SEP+(int)b.getMinY()+SEP+(int)b.getMaxX()+SEP+(int)b.getMaxY();
    }
    
    public static Box getContainingBox(Set boxes) {
        if (boxes.isEmpty()) return null;
        int minx=Integer.MAX_VALUE, miny=Integer.MAX_VALUE;
        int maxx=Integer.MIN_VALUE, maxy=Integer.MIN_VALUE;
        Iterator it = boxes.iterator();
        while (it.hasNext()) {
            Box box = (Box)it.next();
            minx = Math.min(minx, (int)box.getMinX()); miny = Math.min(miny, (int)box.getMinY());
            maxx = Math.max(maxx, (int)box.getMaxX()); maxy = Math.max(maxy, (int)box.getMaxY());
        }
        return new Box(minx, miny, maxx, maxy);
    }
    
    //private List vertices;
    private Point2D.Double vTopLeft;
    private Point2D.Double vTopRight;
    private Point2D.Double vBottomLeft;
    private Point2D.Double vBottomRight;
    
    /** Creates a new instance of Box */
    public Box(int minx, int miny, int maxx, int maxy) {
        super(minx, miny, maxx-minx, maxy-miny);
        this.vTopLeft = new Point2D.Double(minx,miny);
        this.vTopRight = new Point2D.Double(maxx,miny);
        this.vBottomLeft = new Point2D.Double(minx,maxy);
        this.vBottomRight = new Point2D.Double(maxx,maxy);
    }
    
    public Point2D getCenter() {
        return new Point2D.Double(getCenterX(), getCenterY());
    }
    
    public static double distance(Box box1, Box box2)  { return box1.distance(box2);  }
    public static double alignment(Box box1, Box box2) { return box1.alignment(box2); }
    
    public static boolean isInBetween(Box box1, Box boxIn, Box box2) {
        return boxIn.isInBetween(box1, box2);
    }
    
    public double alignment(Box toBox) {
        double min = java.lang.Double.MAX_VALUE;
        min = Math.min(min,topAlignment(toBox));
        min = Math.min(min,botAlignment(toBox));
        min = Math.min(min,leftAlignment(toBox));
        min = Math.min(min,rightAlignment(toBox));
        min = Math.min(min,centerAlignment(toBox));
        log.finer("Alignment: "+this+" ; "+toBox+" is: "+min);
        return min;
    }
    
    private double topAlignment(Box toBox) {
        return xAlignment(this.vTopLeft, toBox.vTopRight);
    }
    
    private  double botAlignment(Box toBox) {
        return xAlignment(this.vBottomLeft,toBox.vBottomRight);
    }
    
    private  double leftAlignment(Box toBox) {
        return yAlignment(this.vTopLeft,toBox.vBottomLeft);
    }
    
    private  double rightAlignment(Box toBox) {
        return yAlignment(this.vTopRight,toBox.vBottomRight);
    }
    
    private  double centerAlignment(Box that) {
        return Math.min( xAlignment(this.getCenter(), that.getCenter() ), yAlignment( this.getCenter(), that.getCenter() ) );
    }
    
    private double xAlignment(Point2D p1, Point2D p2) {
        // it is not clear why java.lang.Math.atan2 doesn't do what we expected
        // so let's code it...
        double dx = Math.abs( p2.getX() - p1.getX() );
        double dy = Math.abs( p2.getY() - p1.getY() );
        return ( dx == 0  ? 0 : Math.atan( dy / dx ));
    }
    
    private double yAlignment(Point2D p1, Point2D p2) {
        // it is not clear why java.lang.Math.atan2 doesn't do what we expected
        // so let's code it...
        double dx = Math.abs( p2.getX() - p1.getX() );
        double dy = Math.abs( p2.getY() - p1.getY() );
        return ( dy == 0 ? 0 : Math.atan( dx / dy ));
    }
    
    public boolean isInBetween(Box b1, Box b2) {
        //find the coordinates of a rectangle covering the area "in between" the two boxes
        double minx = getMin(b1.getMinX(), b1.getMaxX(), b2.getMinX(), b2.getMaxX());
        double maxx = getMax(b1.getMinX(), b1.getMaxX(), b2.getMinX(), b2.getMaxX());
        double miny = getMin(b1.getMinY(), b1.getMaxY(), b2.getMinY(), b2.getMaxY());
        double maxy = getMax(b1.getMinY(), b1.getMaxY(), b2.getMinY(), b2.getMaxY());
        //check if this box intersects that rectangle
        return this.intersects(minx,miny,maxx-minx,maxy-miny);
    }
    
    final private double getMin(double min1, double max1, double min2, double max2) {
        return (overlap(min1,max1,min2,max2) ? Math.max(min1,min2) : Math.min(max1,max2));
    }
    
    final private double getMax(double min1, double max1, double min2, double max2) {
        return (overlap(min1,max1,min2,max2) ? Math.min(max1,max2) : Math.max(min1,min2));
    }
    
    final private boolean overlap(double min1, double max1, double min2, double max2) {
        return Math.max(min1, min2) < Math.min(max1, max2);
    }
    
    //Used by VisualLabeller to collect Boxes in TreeSet
    public int compareTo(Object o) {
        Box that = (Box)o;
        if (this.isAbove(that)) return -1;
        if (that.isAbove(this)) return +1;
        if (this.isOnLeft(that)) return -1;
        if (that.isOnLeft(this)) return +1;
        return super.hashCode()-that.hashCode(); // they are the same???
    }
    
    public double distance(Box that) {
        double d = 0;
        if (this.isXAlined(that)) {
            return (this.isOnLeft(that))? that.getMinX() - this.getMaxX(): this.getMinX() - that.getMaxX();
        } else if (this.isYAlined(that)) {
            return (this.isAbove(that)) ? that.getMinY() - this.getMaxY(): this.getMinY() - that.getMaxY();
        }
        return this.getCenter().distance(that.getCenter());
    }
    
    public boolean isXAlined(Box that) {
        return this.overlap(this.getMinY(),this.getMaxY(), that.getMinY(), that.getMaxY());
    }
    
    public boolean isYAlined(Box that) {
        return this.overlap(this.getMinX(),this.getMaxX(), that.getMinX(), that.getMaxX());
    }
    
    public boolean isAbove(Box that) {
        return this.getMaxY() <= that.getMinY();
    }
    
    public boolean isOnLeft(Box that) {
        return this.getMaxX() <= that.getMinX();
    }
    
    public boolean equals(Object o) {
        Box that = (Box)o;
        return this.getMinX()==that.getMinX() && this.getMinY()==that.getMinY() &&
        this.getMaxX()==that.getMaxX() && this.getMaxY()==that.getMaxY();
    }
    
    public int hashCode() {
        return (int)(getMinX()+getMinY()+getMaxX()+getMaxY());
    }
    
    public String toString() {
        return "[("+getMinX()+","+getMinY()+"), ("+getMaxX()+","+getMaxY()+")]";
    }
    
}
