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
        println(intersect);
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

    public ArrayList<Point3D> intersection(Line3D line, ArrayList<Point3D> point) {
        Iterator<Shape3D> iter = components.iterator();
        count = 0;
        while (iter.hasNext()) {
            if (iter.next().hasIntersection(line)) {
                count += iter.next().intersection(line, point);
            }
        }
        return count;
    }

}

public class Calorimeter extends Detector {

    public Calorimeter() {
        this.name = "Cal";
        initCal();
    }

    //rotate 25 deg in y
    //convert deg to rad

    private void initCal(){
        ArrayList<Shape3D> list = new ArrayList<>();
        Triangle3D slice = new Triangle3D(0.0, 0.0 , 50.0, 197.1, 385.2, 50.0, -197.1,
                385.2, 50.0);
        slice.rotateZ(30 * 0.0174533);
        slice.rotateY(25 * 0.0174533);
        for(int i = 0; i < 6; i++){
            Shape3D calSlice = new Shape3D();
            calSlice.addFace(new Triangle3D(slice));
            calSlice.moveTo(0,0,700);
            this.components.add(calSlice);
            slice.rotateZ(60 * 0.0174533);
        }
    }

}
