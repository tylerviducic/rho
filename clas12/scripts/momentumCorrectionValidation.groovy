import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H2F hWPhi = new H2F("WPhi", 100, 0, 5,180, -180, 180);
hWPhi.setTitle("W vs Phi");

H2F hWPhiCor = new H2F("WPhiCor", 100, 0, 5,180, -180, 180);
hWPhiCor.setTitle("W vs Phi Corrected");

String dir = "/lustre19/expphy/cache/clas12/rg-a/production/recon/fall2018/torus-1/pass1/v0/dst/train/skim4/";
HipoChain reader = new HipoChain();
reader.addDir(dir, "*skim4_00512*.hipo");
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

int eventCounter = 0;

while (reader.hasNext() && eventCounter < 10000000){
    eventCounter++;
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle electron = physicsEvent.getParticleByPid(11, 0);
    Particle correctedElectron = correctedElectron(Particle.copyFrom(electron));

    Particle unCor = physicsEvent.getParticle("[b] + [t] - [11]");
    Particle cor = physicsEvent.getParticle("[b] + [t]");
    cor.combine(Particle.copyFrom(correctedElectron), -1);

    hWPhi.fill(unCor.mass(), Math.toDegrees(electron.phi()));
    hWPhiCor.fill(cor.mass(), Math.toDegrees(correctedElectron.phi()));
}

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2, 1);
c1.cd(0).draw(hWPhi);
c1.cd(1).draw(hWPhiCor);


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public static Particle correctedElectron(Particle electron){
    Particle correctedElectron = Particle.copyFrom(electron);
    double momentum = electron.p();
//    correctedElectron.setP( momentum / (6.8123 - 2.6613 * momentum + 0.41056 * momentum * momentum - 0.021082 * momentum * momentum * momentum));
    correctedElectron.setP(momentum / (4.88 - 1.792 * momentum + 0.2815 * momentum * momentum - 0.01476 * momentum * momentum * momentum));
    return correctedElectron;
}