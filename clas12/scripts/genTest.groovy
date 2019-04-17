





// Classes

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class PhysicsReaction implements IReaction{

    FunctionIntegral2D  energyCosCMDist;
    int     beamParticleID;
    double  beamEnergyMinimum = 1.0; // set tp 1.3
    double  beamEnergyMaximum = 5.0; //set to 3.6 (i think)
    boolean beamTypePhoton;
    int     targetParticleID;
    int[]   producedParticles;
    double  targetPositionMin;
    double  targetPositionMax;
    ArrayList<IDecay> decayList;

    public PhysicsReaction()
    {
        producedParticles = new int[2];
        beamTypePhoton    = true;
        beamParticleID    = 22;
        targetParticleID  = 2212;
        targetPositionMin = -30.0; //is this accurate for g11? check
        targetPositionMax = +10.0;
        energyCosCMDist = new FunctionIntegral2D();
        decayList = new ArrayList<IDecay>();
        EnergyCosFlat func = new EnergyCosFlat();
        this.setWeightFunction(func);
    }

    public void setBeamEnergy(double min, double max)
    {
        beamEnergyMinimum = min;
        beamEnergyMaximum = max;
    }

    public void setParticles(int pid1, int pid2)
    {
        System.out.println("====> Physics Generator Set PID " + pid1 + " : " + pid2);
        producedParticles[0] = pid1;
        producedParticles[1] = pid2;
    }

    public void setParticles(String name1, String name2)
    {
        PDGParticle prod1 = PDGDatabase.getParticleByName(name1);
        if(prod1==null)
        {
            System.out.println("----> error. pdg does not contain name=[" + name1 + "]");
            return;
        }

        PDGParticle prod2 = PDGDatabase.getParticleByName(name2);
        if(prod1==null)
        {
            System.out.println("----> error. pdg does not contain name=[" + name2 + "]");
            return;
        }
        this.setParticles(prod1.pid(),prod2.pid());
    }

    public BasicLorentzVector getBeam(double energy)
    {
        //double energy = beamEnergyMinimum + Math.random()*
        //(beamEnergyMaximum-beamEnergyMinimum);
        PDGParticle particle = PDGDatabase.getParticleById(beamParticleID);
        BasicLorentzVector vectB = new BasicLorentzVector();
        vectB.setPxPyPzM(0.0, 0.0, energy, particle.mass());
        return vectB;
    }

    BasicVector getProductionVertex()
    {
        BasicVector vert = new BasicVector();
        double vz = targetPositionMin +
                Math.random()*(targetPositionMax-targetPositionMin);
        vert.setXYZ(0.0, 0.0, vz);
        return vert;
    }

    @Override
    public void generate(PhysicsEvent event)
    {
        this.createEvent(event);
        this.decay(event);
    }

    void createEvent(PhysicsEvent event)
    {
        event.clear();

        PDGParticle particle = PDGDatabase.getParticleById(targetParticleID);

        BasicVector prodVertex = getProductionVertex();

        BasicLorentzVector vectT = new BasicLorentzVector(0.0,0.0,0.0,particle.mass());
        double[] energyAndcos = this.getEnergyCos();

        BasicLorentzVector vectCM = this.getBeam(energyAndcos[0]);

        event.setBeamParticle(new Particle(beamParticleID,
                vectCM.px(),vectCM.py(),vectCM.pz(),
                prodVertex.x(),prodVertex.y(),prodVertex.z()
        ));
        event.setTargetParticle(new Particle(targetParticleID,
                vectT.px(),vectT.py(),vectT.pz(),
                prodVertex.x(),prodVertex.y(),prodVertex.z()
        ));

        vectCM.add(vectT);

        PDGParticle decayP1 = PDGDatabase.getParticleById(producedParticles[0]);
        PDGParticle decayP2 = PDGDatabase.getParticleById(producedParticles[1]);

        double decayCosTheta = energyAndcos[1];
        double decayPhi      = Math.random()*2.0*Math.PI - Math.PI;


        BasicLorentzVector[] decayVectors = DecayKinematics.getDecayParticles(vectCM,
                decayP1.mass(), decayP2.mass(), Math.acos(decayCosTheta), decayPhi);


        BasicVector boostV = vectCM.boostVector();
        decayVectors[0].boost(boostV);
        decayVectors[1].boost(boostV);

        event.addParticle(new Particle(decayP1.pid(),
                decayVectors[0].px(),
                decayVectors[0].py(),
                decayVectors[0].pz(),
                prodVertex.x(),prodVertex.y(),prodVertex.z()
        ));

        event.addParticle(new Particle(decayP2.pid(),
                decayVectors[1].px(),
                decayVectors[1].py(),
                decayVectors[1].pz(),
                prodVertex.x(),prodVertex.y(),prodVertex.z()
        ));
    }

    double[] getEnergyCos()
    {
        return energyCosCMDist.getRandom();
    }

    @Override
    public void init() {

    }

    @Override
    public void decay(PhysicsEvent event) {
        for(int loop = 0; loop < decayList.size(); loop++)
        {
            decayList.get(loop).decayParticles(event);
        }
    }

    @Override
    public void setWeightFunction(IFunction func) {
        System.out.println("----> Initializing energy vs coscm function");
        energyCosCMDist.setFunction(func);
        System.out.println("----> function initialization complete.");
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addDecay(IDecay decay) {
        decayList.add(decay);
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ThreeBodyDecay implements IDecay {
    int decayParticleID;
    int[] productParticleID;

    FunctionIntegral particleDecayPhaseFunction;

    public ThreeBodyDecay()
    {

        decayParticleID   = 221;
        productParticleID = new int[3];
        productParticleID[0] =  211;
        productParticleID[1] = -211;
        productParticleID[2] =   22;
        particleDecayPhaseFunction = new FunctionIntegral();
        particleDecayPhaseFunction.setFunction(new FuncUniform());
    }

    public ThreeBodyDecay(String parent, String child1, String child2, String child3)
    {
        productParticleID = new int[3];
        particleDecayPhaseFunction = new FunctionIntegral();
        particleDecayPhaseFunction.setFunction(new FuncUniform());
        this.setDecayParticle(parent);
        this.setDecayProducts(child1, child2, child3);
    }

    @Override
    public void weightFunction(IFunction func) {
        particleDecayPhaseFunction.setFunction(func);
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDecayParticle(int id) {
        if(PDGDatabase.getParticleById(id)==null)
        {
            System.out.println("ThreeBodyDecay:setDecayParticle ERROR ---> can not find "
                    + " particle with pid="+id);
            return;
        }
        decayParticleID = id;
    }

    @Override
    public void setDecayProducts(int pid1, int pid2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDecayProducts(int pid1, int pid2, int pid3) {

        if(PDGDatabase.getParticleById(pid1)==null){
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle id " + pid1);
            return;
        }
        if(PDGDatabase.getParticleById(pid2)==null){
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle id " + pid2);
            return;
        }
        if(PDGDatabase.getParticleById(pid3)==null){
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle id " + pid3);
            return;
        }
        productParticleID[0] = pid1;
        productParticleID[1] = pid2;
        productParticleID[2] = pid3;
    }

    double getMassFunction(double mass, double m1, double m2, double m3)
    {
        double mmin = m1 + m2;
        double mmax = mass - m3;
        if(mmax<mmin)
        {
            System.out.println("---> error the particle m="+mass
                    + " can not decay to m = [" + m1 + " , " + m2
                    + " , " + m3 + " ]");
            return 0;
        }
        double randomMass = particleDecayPhaseFunction.getRandom();
        return mmin + Math.abs(mmax-mmin)*randomMass;
    }

    @Override
    public void decayParticles(PhysicsEvent event) {
        int index = event.getParticleIndex(decayParticleID, 0);
        if(index<0)
        {
            System.out.println("----> particle with pid="+decayParticleID+" does not exist in the event");
            return;
        }

        PDGParticle parent = PDGDatabase.getParticleById(decayParticleID);
        PDGParticle dp1 = PDGDatabase.getParticleById(productParticleID[0]);
        PDGParticle dp2 = PDGDatabase.getParticleById(productParticleID[1]);
        PDGParticle dp3 = PDGDatabase.getParticleById(productParticleID[2]);

        double massDecayCombo = getMassFunction(parent.mass(),dp1.mass(),dp2.mass(),dp3.mass());

        double decayCosThetaDL = Math.random()*2.0-1.0;
        double decayPhiDL      = Math.random()*2.0*Math.PI - Math.PI;

        Particle decayPart = event.getParticle(index);
        BasicLorentzVector[] decayGDiL = DecayKinematics.getDecayParticlesLab(decayPart.vector(),
                massDecayCombo,dp3.mass(),Math.acos(decayCosThetaDL),decayPhiDL);

        double decayCosThetaStep2 = Math.random()*2.0-1.0;
        double decayPhiStep2      = Math.random()*2.0*Math.PI - Math.PI;

        BasicLorentzVector[]  decayGDStep2 = DecayKinematics.getDecayParticlesLab(decayGDiL[0],
                dp1.mass(),dp2.mass(),Math.cos(decayCosThetaStep2),decayPhiStep2);



        event.addParticle(new Particle(dp1.pid(),
                decayGDStep2[0].px(),decayGDStep2[0].py(),decayGDStep2[0].pz(),
                decayPart.vertex().x(),decayPart.vertex().y(),decayPart.vertex().z()));
        event.addParticle(new Particle(dp2.pid(),
                decayGDStep2[1].px(),decayGDStep2[1].py(),decayGDStep2[1].pz(),
                decayPart.vertex().x(),decayPart.vertex().y(),decayPart.vertex().z()));
        event.addParticle(new Particle(dp3.pid(),
                decayGDiL[1].px(),decayGDiL[1].py(),decayGDiL[1].pz(),
                decayPart.vertex().x(),decayPart.vertex().y(),decayPart.vertex().z()));

        index = event.getParticleIndex(decayParticleID, 0);
        event.removeParticle(index);
    }

    @Override
    public void decayParticle(BasicLorentzVector vector) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BasicLorentzVector getParticle(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDecayParticle(String name) {
        PDGParticle dp = PDGDatabase.getParticleByName(name);
        if(dp==null)
        {
            System.out.println("----> error. pdg does not contain name=[" + name + "]");
            return;
        }
        decayParticleID = dp.pid();
    }

    @Override
    public void setDecayProducts(String name1, String name2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDecayProducts(String name1, String name2, String name3) {
        if(PDGDatabase.getParticleByName(name1)==null){
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle name " + name1);
            return;
        }
        if(PDGDatabase.getParticleByName(name2)==null){
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle name " + name2);
            return;
        }
        if(PDGDatabase.getParticleByName(name3)==null){
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle name " + name3);
            return;
        }
        productParticleID[0] = PDGDatabase.getParticleByName(name1).pid();
        productParticleID[1] = PDGDatabase.getParticleByName(name2).pid();
        productParticleID[2] = PDGDatabase.getParticleByName(name3).pid();
    }

}