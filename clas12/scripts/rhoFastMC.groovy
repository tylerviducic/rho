//import org.jlab.clas.fastmc.Clas12FastMC


import org.jetbrains.annotations.NotNull
import org.jlab.geom.prim.Line3D
import org.jlab.geom.prim.Path3D
import org.jlab.geom.prim.Point3D
import org.jlab.geom.prim.Shape3D
import org.jlab.geom.prim.Triangle3D
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.ParticleList
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.LundReader

Calorimeter cal = new Calorimeter();


String dataFile = "/u/group/clas12/mcdata/generated/lund/ppippim/clasdispr.00.e11.000.emn0.75tmn.09.xs65.61nb.113.0001.dat";

H2F hSquare = new H2F("hSquare", "hSquare",100, -500, 500, 100, -500, 500);
TCanvas c1 = new TCanvas("c1", 600, 600);


//Clas12FastMC fmc = new Clas12FastMC(-1, -1);
LundReader reader = new LundReader();
reader.addFile(dataFile);
reader.open();

PhysicsEvent event = new PhysicsEvent();

while(reader.nextEvent(event)){
    ParticleList particles = event.getParticleList();
    for(int i = 0; i < particles.count(); i++){
        Particle particle = particles.get(i);
        StraightLine line = new StraightLine(particle);
        Path3D ppath = line.getPath();
        boolean intersect = cal.hasIntersection(ppath.getLine(0));
        //println(intersect);
        ArrayList<Point3D> inters = new ArrayList<Point3D>();
        int count = cal.intersection(ppath.getLine(0), inters);
        if(intersect){
            for(Point3D point : inters){
                hSquare.fill(point.x(), point.y());
            }
        }
    }
}

c1.draw(hSquare);


public class StraightLine {

    Path3D path = new Path3D();
    Particle myParticle = new Particle();


    public StraightLine(Particle myParticle) {
        this.myParticle = myParticle;
        getStartPoint(myParticle);
    }

    public void getStartPoint(Particle particle){
        this.path.addPoint(particle.vertex().x(), particle.vertex().y(), particle.vertex().z());
    }

    private void makePath(){
        this.path.addPoint(1500 * myParticle.px(), 1500*myParticle.py(), 1500* myParticle.pz());
    }

    public Path3D getPath(){
        makePath();
        return this.path;
    }
}

public class Calorimeter {

    ArrayList<Shape3D> detector = initCal();

    private ArrayList<Shape3D> initCal(){

        ArrayList<Shape3D> newDetector = new ArrayList<Shape3D>();
        Triangle3D slice = new Triangle3D(0.0, 0.0 , 50.0, 197.1, 385.2, 50.0, -197.1,
                385.2, 50.0);
        for( int i = 0; i < 6; i ++){
            Shape3D dslice = new Shape3D();
            dslice.addFace(new Triangle3D(slice));
            dslice.moveTo(0,0,750);
            slice.rotateZ(i * 60);
            newDetector.add(dslice);
        }
        return newDetector;
    }

    public Calorimeter() {
    }

    public boolean hasIntersection(Line3D line){
        for (int i = 0; i < this.detector.size(); i ++){
            if(this.detector.get(i).hasIntersection(line)){
                return true;
            }
        }
        return false;
    }

    public int intersection(Line3D line, List<Point3D> intersections){
        int count = 0;
        for(int i = 0; i < this.detector.size(); i++){
            count += detector.get(i).intersection(line, intersections);
        }
        return count;
    }
}
