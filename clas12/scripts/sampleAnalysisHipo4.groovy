import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

String dataFile = "/work/clas12/devita/ctofCalib/rec_004013.hipo";


H1F hMxpUncut = new H1F("hMxPUncut", 200, 0.4, 1);
hMxpUncut.setTitle("mx_P");
hMxpUncut.setFillColor(43);

H1F hMxpcut = new H1F("hMxPUncut", 200, 0.4, 1);
hMxpcut.setTitle("mx_P");
hMxpcut.setFillColor(43);

H1F hMx2_PePipPim = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPim.setTitle("Missing mass squared of pePipPim");

H1F hMP_PePipPim = new H1F("hMP_PiPipPim", 210, -0.1, 2);
hMP_PePipPim.setTitle("Missing momentum of pePipPim");

H1F himPipPimGamUncut = new H1F("himPipPimGamUncut", 150, 0.0, 1.5);
himPipPimGamUncut.setTitle("IM_PipPimXn");
himPipPimGamUncut.setFillColor(43);

H1F hcos = new H1F("hcos", 20, 0.99, 1);
hcos.setTitle("hcos");


TCanvas c1 = new TCanvas("c1", 500, 600);
TCanvas c2 = new TCanvas("c2", 500, 600);
TCanvas c3 = new TCanvas("c3", 500, 600);
TCanvas c4 = new TCanvas("c4", 500, 600);
TCanvas c5 = new TCanvas("c5", 500, 600);
TCanvas c6 = new TCanvas("c6", 500, 600);


c1.getCanvas().initTimer(1000);
c2.getCanvas().initTimer(1000);
c3.getCanvas().initTimer(1000);
c4.getCanvas().initTimer(1000);
c5.getCanvas().initTimer(1000);
c6.getCanvas().initTimer(1000);


c1.draw(hMxpUncut);
c2.draw(hMxpcut);
c3.draw(hMx2_PePipPim);
c4.draw(hMP_PePipPim);
c5.draw(himPipPimGamUncut);
c6.draw(hcos);

// Begin Analysis //

double beamEnergy = 10.6

HipoReader reader = new HipoReader();
reader.open(inputFile);

Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Event event = new Event();

EventFilter filter = new EventFilter("11:2212:211:-211:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particles);

    PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);


}