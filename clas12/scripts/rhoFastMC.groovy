import org.jlab.geom.prim.Line3D
import org.jlab.geom.prim.Path3D
import org.jlab.geom.prim.Point3D
import org.jlab.geom.prim.Shape3D
import org.jlab.geom.prim.Triangle3D
import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.ParticleList
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.LundReader

import java.lang.reflect.Array

Calorimeter cal = new Calorimeter();
cal.initCal();

DriftChamberSector dcSector = new DriftChamberSector(0.3861, 4.694, 228.078);
dcSector.initDCSector();


String dataFile = "/u/group/clas12/mcdata/generated/lund/ppippim/clasdispr.00.e11.000.emn0.75tmn.09.xs65.61nb.113.0001.dat";

H2F hSquare = new H2F("hSquare", "hSquare",100, -600, 600, 100, -600, 600);
TCanvas c1 = new TCanvas("c1", 600, 600);
TCanvas c2 = new TCanvas("c2", 600, 600);
TCanvas c3 = new TCanvas("c3", 600, 600);

H1F hMxp = new H1F("hMxp", "hMxp", 150, 0 , 1.5);
H1F hMxpWithHits = new H1F("hMxpWithHits", "hMxpWithHits", 150, 0 , 1.5);

//Clas12FastMC fmc = new Clas12FastMC(-1, -1);
LundReader reader = new LundReader();
reader.acceptStatus((int)(1));
reader.addFile(dataFile);
reader.open();

PhysicsEvent event = new PhysicsEvent();

while(reader.nextEvent(event)){
    event.setBeamParticle(new Particle(11, 0, 0, 11));
    event.setTargetParticle(new Particle(2212, 0, 0, 0));

    ParticleList particles = event.getParticleList();

    Particle p = event.getParticleByPid(2212,1);
    Particle e = event.getParticleByPid(11,1);
    Particle pip = event.getParticleByPid(211,0);
    Particle pim = event.getParticleByPid(-211,0);

    Particle mxp = event.getParticle("[b] + [t] - [11,1] - [2212,1]");

    for(int i = 0; i < particles.count(); i++){

        Particle particle = particles.get(i);
        StraightLine line = new StraightLine(particle);
        Path3D ppath = line.getPath();
        boolean intersect = dcSector.hasIntersection(ppath.getLine(0));
        println(intersect);
        ArrayList<Point3D> inters = new ArrayList<Point3D>();
        int count = dcSector.intersection(ppath.getLine(0), inters);
        if(intersect){
            for(Point3D point : inters){
                hSquare.fill(point.x(), point.y());
            }
        }
    }

    StraightLine pLine = new StraightLine(p);
    StraightLine eLine = new StraightLine(e);
    StraightLine pipLine = new StraightLine(pip);
    StraightLine pimLine = new StraightLine(pim);

    Path3D pPath = pLine.getPath();
    Path3D ePath = eLine.getPath();
    Path3D pipPath = pipLine.getPath();
    Path3D pimPath = pimLine.getPath();

    hMxp.fill(mxp.mass());

    if(cal.hasIntersection(pPath.getLine(0)) && cal.hasIntersection(ePath.getLine(0)) &&
            (cal.hasIntersection(pipPath.getLine(0)) || cal.hasIntersection(pimPath.getLine(0)))){
        hMxpWithHits.fill(mxp.mass());
    }

}

H1F acceptance = H1F.divide(hMxpWithHits, hMxp);

//c1.draw(hMxp);
//c2.draw(hMxpWithHits);
//c3.draw(acceptance.getGraph());
c1.draw(hSquare);

// ################################################################################################################## //
// ################################################################################################################## //


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

public class Detector {
    String name;
    ArrayList<Shape3D> components = new ArrayList<>();

    public Detector(){}

    public Detector(String name){
        this.name = name;
    }

    public Detector(String name, ArrayList<Shape3D> components) {
        this.components = components;
    }

    public void addComponent(Shape3D shape){
        this.components.add(shape);
    }

    public void removeComponent(int sector){
        this.components.remove(sector);
    }

    public void removeComponent(Shape3D shape){
        this.components.remove(shape);
    }

    public Shape3D getComponent(int sector){
        return this.components.get(sector);
    }

    public boolean hasIntersection(Line3D line){
        Iterator<Shape3D> iter = components.iterator();
        while (iter.hasNext()){
            if (iter.next().hasIntersection(line)){
                return true;
            }
        }
        return false;
    }

    public int intersection(Line3D line, ArrayList<Point3D> point) {
        Iterator<Shape3D> iter = components.iterator();
        int count = 0;
        while (iter.hasNext()) {
            Shape3D detectorComponent = iter.next();
            if (detectorComponent.hasIntersection(line)) {
                count += detectorComponent.intersection(line, point);
            }
        }
        return count;
    }

}

public class Calorimeter extends Detector {

    double distance = 721.7;
    double tilt     = 25.0;

    public Calorimeter() {
        this.name = "Cal";
    }

    //b = 305.013
    //a = 86.179
    //translate to 721.723
    //rotate 25 deg in y
    //rotate
    //convert deg to rad

    private void initCal(){

        ArrayList<Shape3D> list = new ArrayList<>();

        for(int i = 0; i < 6; i++){
            Triangle3D tri = createSector();
            tri.translateXYZ(0.0,0.0,distance);
            tri.rotateY(Math.toRadians(tilt));
            tri.rotateZ(Math.toRadians(60*i));
            Shape3D  shape = new Shape3D();
            shape.addFace(tri);
            this.addComponent(shape);
        }
        /* Triangle3D slice = new Triangle3D(
                      0.0,   0.0, 50.0,
                    197.1, 385.2, 50.0,
                   -197.1, 385.2, 50.0);

           slice.rotateZ(30 * 0.0174533);
           slice.rotateY(25 * 0.0174533);
           for(int i = 0; i < 6; i++){
               Shape3D calSlice = new Shape3D();
               calSlice.addFace(new Triangle3D(slice));
               calSlice.moveTo(0,0,700);
               this.components.add(calSlice);
               slice.rotateZ(60 * 0.0174533);
           }*/
    }


    public Triangle3D createSector(){
        double a = 86.179;
        double b = 305.013;
        return new Triangle3D(
                 a,   -394.2/2, 0.0,
                 a,    394.2/2, 0.0,
                -b,    0.0,  0.0);
    }

}

public class DriftChamberSector extends Detector {

    double wirePlaneDistance;
    double thetaMin;
    double distanceToTarget;
    double tilt = 25;

    public DriftChamberSector(double wirePlaneDistance, double thetaMin, double distanceToTarget) {
        this.thetaMin = thetaMin;
        this.wirePlaneDistance = wirePlaneDistance;
        this.distanceToTarget = distanceToTarget;
    }

    private double height(){
        return 111 * 4 * this.wirePlaneDistance * Math.cos(Math.toRadians(30));
    }

    private double distanceBelowX(){
        return this.distanceToTarget/(Math.tan(Math.toRadians(25 - this.thetaMin)));
    }

    public Triangle3D createSector(){
        return new Triangle3D(height() - distanceBelowX(), -height()/Math.cos(Math.toRadians(30)), 0,
                              height() - distanceBelowX(), height()/Math.cos(Math.toRadians(30)),  0,
                                        -distanceBelowX(), 0,                                  0);
    }


    public void initDCSector(){

        for(int i = 0; i < 6; i++){
            Triangle3D sector = createSector();
            sector.translateXYZ(0,0,distanceToTarget);
            sector.rotateX(Math.toRadians(90));
//            sector.rotateY(Math.toRadians(tilt));
//            sector.rotateZ(Math.toRadians(60*i));
            Shape3D shape = new Shape3D();
            shape.addFace(sector);
            this.addComponent(shape);

        }
    }

}