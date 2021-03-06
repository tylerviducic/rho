

void Rho1(){
  
  TChain *chain = new TChain("g11_PPipPim");
  
  chain->Add("/home/physics/research/g11_photon/g11_PPipPimNtuple_*.root");

  //chain->Add("/home/tylerviducic/research/rho/g11/g11_photon/g11_PPipPimNtuple_*.root");
  
 //chain->Add("/home/physics/research/g11_photon/g11_PPipPimNtuple_*1.root");
  
  
  Int_t nEvent = chain->GetEntries();
  
  
  //###########################################   BEGIN INCOMING DATA   ###########################################################
  
  Int_t runNum;
  chain->SetBranchAddress("runNum", &runNum);
  
  //---------------------------------------------------For Neutral----------------------------
  
  Int_t neutral;
  chain->SetBranchAddress("neutral", &neutral);
  
  
  //----------------------------------------------------For Incoming beam---------------------------------------------------------
  
  
  Double_t E_g;
  
  chain->SetBranchAddress("E_g", &E_g);
  
  //----------------------------------------------------For Proton-------------------------------------------------------------------
  Double_t px_P, py_P, pz_P, vz_P;
  
  chain->SetBranchAddress("px_P", &px_P);
  chain->SetBranchAddress("py_P", &py_P);
  chain->SetBranchAddress("pz_P", &pz_P);
  chain->SetBranchAddress("vz_P", &vz_P);
  
  
  //----------------------------------------------------For PiPlus-------------------------------------------------------------------
  Double_t px_Pip, py_Pip, pz_Pip, vz_Pip;
  
  chain->SetBranchAddress("px_Pip", &px_Pip);
  chain->SetBranchAddress("py_Pip", &py_Pip);
  chain->SetBranchAddress("pz_Pip", &pz_Pip);
  chain->SetBranchAddress("vz_Pip", &vz_Pip);
  
  //----------------------------------------------------For PiMinus--------------------------------------------------------------
  Double_t px_Pim, py_Pim, pz_Pim, vz_Pim;
  
  chain->SetBranchAddress("px_Pim", &px_Pim);
  chain->SetBranchAddress("py_Pim", &py_Pim);
  chain->SetBranchAddress("pz_Pim", &pz_Pim);
  chain->SetBranchAddress("vz_Pim", &vz_Pim);
  
  //----------------------------------------------------For Outgoing photon ----------------------------------------------------------------
  Double_t px_G, py_G, pz_G;
  
  chain->SetBranchAddress("px_G", &px_G);
  chain->SetBranchAddress("py_G", &py_G);
  chain->SetBranchAddress("pz_G", &pz_G);
  
  
  //----------------------------------------------------For Missing Mass -------------------------------------------------------------------
  
  Double_t mm_P, mm_Pip, mm_Pim, mm_G;
  Double_t mm_PPipPimG, mm_PPipPim, mm_PipPimG, mm_PipPim;
  Double_t mm_PPipG, mm_PPimG, mm_PPip, mm_PPim, mm_PG, mm_PipG, mm_PimG;
  
  chain->SetBranchAddress("mm_P", &mm_P);
  chain->SetBranchAddress("mm_Pip", &mm_Pip);
  chain->SetBranchAddress("mm_Pim", &mm_Pim);
  chain->SetBranchAddress("mm_G", &mm_G);
  
  chain->SetBranchAddress("mm_PPipPimG", &mm_PPipPimG);
  chain->SetBranchAddress("mm_PPipPim", &mm_PPipPim);
  chain->SetBranchAddress("mm_PipPimG", &mm_PipPimG);
  chain->SetBranchAddress("mm_PipPim", &mm_PipPim);
  
  chain->SetBranchAddress("mm_PPipG", &mm_PPipG);
  chain->SetBranchAddress("mm_PPimG", &mm_PPimG);
  chain->SetBranchAddress("mm_PPip", &mm_PPip);
  chain->SetBranchAddress("mm_PPim", &mm_PPim);
  chain->SetBranchAddress("mm_PG", &mm_PG);
  chain->SetBranchAddress("mm_PipG", &mm_PipG);
  chain->SetBranchAddress("mm_PimG", &mm_PimG);
  
  //-----------------------------For Invariant Mass ----------------------------
  
  Double_t m_PG, m_PipG, m_PimG, m_PPip, m_PPim, m_PipPim;
  Double_t m_PPipG, m_PPimG, m_PipPimG;
  Double_t m_PPipPimG, m_PPipPim;
  
  chain->SetBranchAddress("m_PG", &m_PG);
  chain->SetBranchAddress("m_PipG", &m_PipG);
  chain->SetBranchAddress("m_PimG", &m_PimG);
  chain->SetBranchAddress("m_PPip", &m_PPip);
  chain->SetBranchAddress("m_PPim", &m_PPim);
  chain->SetBranchAddress("m_PipPim", &m_PipPim);
  
  chain->SetBranchAddress("m_PPipG", &m_PPipG);
  chain->SetBranchAddress("m_PPimG", &m_PPimG);
  chain->SetBranchAddress("m_PipPimG", &m_PipPimG);
  
  chain->SetBranchAddress("m_PPipPimG", &m_PPipPimG);
  chain->SetBranchAddress("m_PPipPim", &m_PPipPim);
  
  //------------------------------missing energy-------------------------------
  
  Double_t me_PPipPim, me_PPipPimGam;
  
  chain->SetBranchAddress("me_PPipPim", &me_PPipPim);
  
  
  //###########################################   END INCOMING DATA   ###############################################################
  
  
  //###########################################   BEGIN OUTGOING DATA   ##############################################################
  
  
  TNtuple *T = new TNtuple("g11_Ntuple","g11_Ntuple","Pp:Ppip:Ppim:Pgam:P_theta:Pip_theta:Pim_theta:Gam_theta:P_phi:Pip_phi:Pim_phi:Gam_phi:mx_P:me_PPipPim:beam:cosTheta:gamEta:IM_PipPim:IM_PipPimGam:IM2_PipPim:phi:vz_P:vz_Pip:vz_Pim:runNum:px_G:py_G:pz_G:px_Pip:py_Pip:pz_Pip:px_Pim:py_Pim:pz_Pim:mx2_PPipPimGam:mx_PPipPimGam:px_P:py_P:pz_P:mx2_PPipPim");
  
  Float_t buffer[50];
  
  
  TLorentzVector vT, vbeam, vP, vPip, vPim, vGam;
  TLorentzVector vBT, vEtaRest, vGamEta, vPipEta, vPimEta, vEtaEta, vIM_PipPim_Eta;
  TLorentzVector vMM_P, vMM_PipPim, vMM_PipPimGam, vMM_PPipPim, vMM_PPipPimGam;
  TLorentzVector vIM_PipPim, vIM_PipPimGam;
  
  TLorentzVector vMM_P_boost;
  TVector3 Etaboost;
  
  Double_t p2_P,p2_Pip,p2_Pim,p2_Gam,Pp,Ppip,Ppim,Pgam,Eproton,Epip,Epim;
  
  Double_t beam, W;
  
  Double_t mx_P,mx_PipPim,mx_PipPimGam,mx_PPipPim,mx_PPipPimGam,mx2_PPipPim,mx2_PPipPimGam,mxProton;
  
  Double_t IM_PipPim,IM_PipPimGam,IM2_PipPim,IM2_PipPimGam;
  
  Double_t IM2_PipPim_Eta,tmand;
  
  Double_t gamEta;
  
  Double_t cosTheta, phi;
  
  Double_t P_theta,Pip_theta,Pim_theta,Gam_theta,P_phi,Pip_phi,Pim_phi,Gam_phi,Rho_phi;
  
  Double_t px_rho, py_rho, pz_rho;
  
  Double_t mxP_pass, mxP_tight_pass, mx2_PPipPim_pass, gamEta_pass, gamEtaP_pass, gamEtaP_tight_pass, IM2_PipPim_eta_pass, IM2_PipPim_etaP_pass, costheta_pass, eBeam_pass, mx2_PPipPimGam_pass;
  
  
  Double_t gamEtaP_pass1, gamEtaP_pass2, gamEtaP_pass3, gamEtaP_pass4,gamEtaP_pass_low,gamEtaP_pass_high;
  
  Double_t good_run_pass;
  
  Double_t M_P = 0.938272;   //Proton
  Double_t M_Pi = 0.139570;  //Pion
  Double_t M_Eta = 0.547853;  //Eta
  Double_t M_Rho = 0.769;  //Rho
  Double_t M_EtaP = 0.95778;  //Eta_Prime
  Double_t M_Elec = 0.000510999; //Electron
  
  vT.SetPxPyPzE(0.0, 0.0, 0.0, M_P);
  
  //-------making a tree ------------
  TTree *dataTree = new TTree("dataTree","Var");
  
  dataTree->Branch("beam",&beam,"beam/D");
  dataTree->Branch("me_PPipPim",&me_PPipPim,"me_PPipPim/D");
  dataTree->Branch("mx2_PPipPimGam",&mx2_PPipPimGam,"mx2_PPipPimGam/D");
  dataTree->Branch("mx2_PPipPim",&mx2_PPipPim,"mx2_PPipPim/D");
  dataTree->Branch("cosTheta",&cosTheta,"cosTheta/D");
  
  dataTree->Branch("IM_PipPim",&IM_PipPim,"IM_PipPim/D");
  dataTree->Branch("IM_PipPimGam",&IM_PipPimGam,"IM_PipPimGam/D");
  dataTree->Branch("IM2_PipPim",&IM2_PipPim,"IM2_PipPim/D");
  dataTree->Branch("mx_P",&mx_P,"mx_P/D");
  
  dataTree->Branch("Pp",&Pp,"Pp/D");
  dataTree->Branch("Ppip",&Ppip,"Ppip/D");
  dataTree->Branch("Ppim",&Ppim,"Ppim/D");
  dataTree->Branch("Pgam",&Pgam,"Pgam/D");
  
  dataTree->Branch("P_theta",&P_theta,"P_theta/D");
  dataTree->Branch("Pip_theta",&Pip_theta,"Pip_theta/D");
  dataTree->Branch("Pim_theta",&Pim_theta,"Pim_theta/D");
  dataTree->Branch("Gam_theta",&Gam_theta,"Gam_theta/D");
  
  dataTree->Branch("P_phi",&P_phi,"P_phi/D");
  dataTree->Branch("Pip_phi",&Pip_phi,"Pip_phi/D");
  dataTree->Branch("Pim_phi",&Pim_phi,"Pim_phi/D");
  dataTree->Branch("Gam_phi",&Gam_phi,"Gam_phi/D");
  
  
  
  //TFile *f = new TFile("/home/tylerviducic/research/rho/g11/PipPimRHo.root","recreate");
  TFile *f = new TFile("/home/physics/research/rho/g11/PipPimRHo.root","recreate");
  

//-----------------------Make Histograms-----------------------------------


  TH1F *mxp[50]; //array of histograms with mx_P in regio of eta
  TH1F *mx2_sig[50]; // array of mx2_PPipPim histograms with signal region of mx_P selected
  TH1F *mx2_sb[50]; // array of  mx2_PPipPim histograms with sideband regions of mx_P selected
  TH1F *mcuts[25]; //array of mx2_PPipPim histograms to test signal for different cuts of mxp
  TH1F *h_mxP = new TH1F("h_mxP", "mxP", 200, .2, 1.1);
  TH1F *h_mxP2 = new TH1F("h_mxP2", "mxP2", 200, .2, 1.1);
  TH1F *subtract[50]; // array of histograms with sidebands subtracted;
  TH1F *h_imPipPim = new TH1F("h_imPipPim", "h_imPipPim", 150, 0, 1);
  TH1F *htMand = new TH1F("tMand", "tMand", 210, -2, 0.1);
  TH1F *h_neutral = new TH1F("neutral", "neutral", 3, 0,3);
  TH1F *h_mx2_PPipPimGam = new TH1F("mx2_PPipPimGam","mx2_PPipPimGam", 100, -.01, .01);
  TH1F *omegaP = new TH1F("omegaP", "omegaP", 100, -0.05, 0.05);
  TH1F *rhoP = new TH1F("rhoP", "rhoP", 100, -0.05, 0.05);
  TH1F *h_1 = new TH1F("h_1", "h_1", 100, -0.05, 0.05);
  
  char hname[61];
  char cname[61];
  char sbname[61];
  char mname[50];
  char mbin[50];
  char sub[61];
  char bin[61];
  char bin_sig[61];
  char bin_sb[61];
  char bin_sub[61];
  double e;
  for (int k=0; k<=50; k++)
    {
      e=0.3+(double(k)/100.0); //connecting histogram number with bin number
      
      sprintf(hname,"h%d",k);
      sprintf(bin, "mx_P in region of rho for abs(IM_PipPim - %.2f) < 0.005",e);
      mxp[k] = new TH1F(hname, bin ,100,0.6,0.9);
      
      sprintf(cname,"signal%d",k);
      sprintf(bin_sig, " abs(IM_PipPim - %.2f) < 0.005",e);
      mx2_sig[k] = new TH1F(cname, bin_sig, 80, -0.05,0.05);
      
      sprintf(sbname,"sideband%d",k);
      sprintf(bin_sb, "abs(IM_PipPim - %.2f) < 0.005",e);
      mx2_sb[k] = new TH1F(sbname, bin_sb, 80, -0.05,0.05);
      
      sprintf(sub,"subtract%d",k);
      sprintf(bin_sub, "subtracted, abs(IM_PipPim-%.2f) < 0.005",e);
      subtract[k] = new TH1F(sub, bin_sub, 80, -0.05,0.05);
      
    }
    
    double z;
    for(int i = 0;  i < 25; i++){
    	z = .005 + double(i) /1000;
    	sprintf(mname, "mxcut %d", i);
    	sprintf(mbin, "signal for cut mxp - 780 > %f", z);
    	mcuts[i] = new TH1F(mname, mbin, 100, -0.05, 0.05);
    }
    
  
  
  for( Int_t i = 0; i <= nEvent; i++){//nEvent
    chain->GetEntry(i);
    if(!(i%1000000)) std::cout << "done " << i << " out of " << nEvent << " ==> " << double(i)*100.0/double(nEvent) << "%" << std::endl;
    
    //-------------------momenta -----------------------------------
    
    p2_P = px_P*px_P + py_P*py_P + pz_P*pz_P;
    p2_Pip = px_Pip*px_Pip + py_Pip*py_Pip + pz_Pip*pz_Pip;
    p2_Pim = px_Pim*px_Pim + py_Pim*py_Pim + pz_Pim*pz_Pim;
    p2_Gam = px_G*px_G + py_G*py_G + pz_G*pz_G;
   
    Pp = sqrt(p2_P);
    Pgam = sqrt(p2_Gam);
    Ppip = sqrt(p2_Pip);
    Ppim = sqrt(p2_Pim);
    
    px_rho = px_Pip + px_Pim + px_G;
    py_rho = py_Pip + py_Pim + py_G;
    pz_rho = pz_Pip + pz_Pim + pz_G;
    
    //---------------------------Energy -----------------------------------
    
    Eproton = sqrt(p2_P + M_P*M_P);
    Epip = sqrt(p2_Pip + M_Pi*M_Pi);
    Epim = sqrt(p2_Pim + M_Pi*M_Pi);
    
    //----------------setting Lorentz Vectors -------------------------------------
    
    vP.SetPxPyPzE(px_P, py_P, pz_P, Eproton);
    vPip.SetPxPyPzE(px_Pip, py_Pip, pz_Pip, Epip);
    vPim.SetPxPyPzE(px_Pim, py_Pim, pz_Pim, Epim);
    vGam.SetPxPyPzE(px_G, py_G, pz_G, Pgam);
    
    beam = E_g;
    
    W=TMath::Sqrt((2*beam*M_P)+(M_P*M_P));//CM energy of the beam
    
    vbeam.SetPxPyPzE(0.0, 0.0, beam, beam);
    
    vBT = vbeam + vT;
    
    //----------------------------------------missing mass vectors----------------------------------------------------------------
    
    vMM_P = (vbeam + vT) - vP;
    vMM_PipPim = (vbeam + vT) - (vPip + vPim);
    vMM_PPipPim = (vbeam + vT) - (vP + vPip + vPim);
    vMM_PPipPimGam = (vbeam + vT) - (vP + vPip + vPim + vGam);
    
    //----------------------------------------Invariant vectors----------------------------------------------------------------
    
    vIM_PipPim = vPip + vPim;
    vIM_PipPimGam = vPip + vPim + vGam;
    
    //----------------------------------------Missing Mass/Invariant Mass------------------------------------------------------
    
    mx_P = mm_P;
    mx_PipPim = mm_PipPim;
    mx_PipPimGam = mm_PipPimG;
    mx_PPipPim = mm_PPipPim;
    mx_PPipPimGam = mm_PPipPimG;
    
    mx2_PPipPim = vMM_PPipPim.M2();
    mx2_PPipPimGam = vMM_PPipPimGam.M2();
    
    IM_PipPim = vIM_PipPim.M();
    IM_PipPimGam = vIM_PipPimGam.M();
    
    IM2_PipPim = vIM_PipPim.M2();
    IM2_PipPimGam = vIM_PipPimGam.M2();
    
    
    
    //------------------------------------Missing Energy -------------------
    
    
    me_PPipPim = vMM_PPipPim.E();
    me_PPipPimGam = vMM_PPipPimGam.E(); 
    
    
    //---------------------------------Azimuthal Angle of Rho-----------------
    
    Rho_phi = (atan(py_rho / px_rho) * 180)/ TMath::Pi();


    //-----------------------------t-mandelston consturction-----------------------

    
    tmand= (2.0 * (M_P * M_P)) - (2.0 * Eproton * M_P); 
	

    //------------------------Cuts and Filling Histograms--------------------------------

	if(Pgam > 0.1 && me_PPipPim > 0.1 && abs(me_PPipPim - Pgam) < 0.1 && abs(mx_P - M_Rho) < 0.6 && abs(mx2_PPipPim) < 0.005){
		h_mx2_PPipPimGam->Fill((beam + M_P - Eproton - Epip - Epim - Pgam) * (beam + M_P - Eproton - Epip - Epim - Pgam) - ((-px_P - px_Pip - px_Pim - px_G) * (-px_P - px_Pip - px_Pim - px_G) + (-py_P - py_Pip - py_Pim - py_G)*(-py_P - py_Pip - py_Pim - py_G) + (beam -pz_P - pz_Pip - pz_Pim - pz_G) * (beam -pz_P - pz_Pip - pz_Pim - pz_G)));
	}
	
	if ( abs(mx_P-M_Rho)< 0.06 && Pgam > 0.1 && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.0005  && Pgam > 0.1 && me_PPipPim - Pgam>-0.01 && me_PPipPim - Pgam < 0.3 && abs(mx2_PPipPim)<0.005){
		htMand->Fill(tmand);
	}
	

	// loop to make mx_P, mx2_PPipPim_sb, and mx2_PPipPim_signal histograms\
	
	double z;
	for(int i = 0; i < 25; i++){
		z = 0.005+double(i) / 1000;
		if(abs(mx_P-M_Rho)< 0.12 && Pgam > 0.1 && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.001  && me_PPipPim - Pgam>-0.1 && me_PPipPim - Pgam < 0.1  && abs(mx_P - 0.780) > z){
			mcuts[i]->Fill(mx2_PPipPim);
		}
	}
	
	if(abs(mx_P-.780)< 0.01 && Pgam > 0.1 && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.001  && me_PPipPim - Pgam>-0.1 && me_PPipPim - Pgam < 0.1){
		omegaP->Fill(mx2_PPipPim);
	}
	
	if(abs(mx_P-M_Rho)< 0.12 && Pgam > 0.1 && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.001  && me_PPipPim - Pgam>-0.1 && me_PPipPim - Pgam < 0.1){
		rhoP->Fill(mx2_PPipPim);
	}
	
	h_1->Add(rhoP,omegaP, 1, -1);

    Int_t k=0;
    double j=0.3;
    double h;
    
    //h_mxP->Fill(mx_P);
    //h_mxP2->Fill(mxProton);


    for  (int k=0; k<=50; k++)
      {
        
	h=0.3+(double(k)/100.0); //relates histogram number (place in array) to bin of IM_PipPim
	if ( Pgam > 0.1 && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.001  && Pgam > 0.1 && me_PPipPim - Pgam>-0.1 && me_PPipPim - Pgam < 0.1 && abs(mx2_PPipPim)<0.005)
	  {
	    if (abs(IM_PipPim-h)<0.005) //selects bin of IM_PipPim
	      {
		mxp[k]->Fill(mx_P); //array of histograms showing signal of rho
	      }
	  }
	  
	/*
	
	if (abs(mx_P-M_Rho)<0.06  && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.001  && Pgam > 0.1 && abs(me_PPipPim - Pgam) < 0.1 && abs(mx_P-0.782)>0.02)
	  {
	    if (abs(IM_PipPim-h)<0.005) //selects bin of IM_PipPim
	      {
		mx2_sig[k]->Fill(mx2_PPipPim); //array of mx2 histograms from signal region of rho
	      }
	  }
	  
	  */
	  
	  	if (abs(mx_P-M_Rho)<0.12 && mx_P < .73  && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.001  && Pgam > 0.1 && abs(me_PPipPim - Pgam) < 0.1 && abs(mx_P-0.782)>0.02)
	  {
	    if (abs(IM_PipPim-h)<0.005) //selects bin of IM_PipPim
	      {
		mx2_sig[k]->Fill(mx2_PPipPim); //array of mx2 histograms from signal region of rho
	      }
	  }
	//remove this when doing sidebands  
	
  
	if (Pgam > .1 && me_PPipPim > 0.1  && abs(me_PPipPim - Pgam)<0.1 && abs(mx2_PPipPimGam)<0.001  && (abs(mx_P-0.680)<0.015 || abs(mx_P-0.850)<0.015) )
	  {
	    if (abs(IM_PipPim-h)<0.005)//Selects bin of IM_PipPim
	      {
		mx2_sb[k]->Fill(mx2_PPipPim); //array of mx2 histograms from sideband regions of eta
	      }
	  }
	
	subtract[k]->Add(mx2_sig[k],mx2_sb[k],1,-1); //used to subtract the sidebands from the signal region for each bin of IM_PipPim
 	
	
	}
      
   


    
    
    //---------------boosting--------------
    
    vMM_P_boost = vMM_P;
    
    vMM_P_boost.Boost(-vBT.BoostVector());
    
    cosTheta = vMM_P_boost.CosTheta();
    
    phi = vMM_P_boost.Phi()*57.29;
    
    //cout << "phi = " << phi << endl;
    
    vGamEta = vGam;
    Etaboost = vMM_P.BoostVector();
    vGamEta.Boost(-Etaboost);
    
    vIM_PipPim_Eta = vIM_PipPim;
    vIM_PipPim_Eta.Boost(-Etaboost);
    
    gamEta = vGamEta.E();
    
    //Double_t gamEta_Cal = (mx_P - (IM_PipPim*IM_PipPim)/mx_P)/2.0;
    
    IM2_PipPim_Eta = vIM_PipPim_Eta.M2();
    
    
    //--------angle btw Prot_CM and beam---------------
    
    //Double_t cosTheta = vbeam.Vect().Dot(vP.Vect())/(vbeam.Vect().Mag()*vP.Vect().Mag());
    
    //--------------------------angular distribution---------------------------------------------
    
    P_theta = vP.Theta()*57.29;
    Pip_theta = vPip.Theta()*57.29;
    Pim_theta = vPim.Theta()*57.29;
    Gam_theta = vGam.Theta()*57.29;
    
    P_phi = vP.Phi()*57.29;
    Pip_phi = vPip.Phi()*57.29;
    Pim_phi = vPim.Phi()*57.29;
    Gam_phi = vGam.Phi()*57.29;
    
    
if((me_PPipPim>0.15) && TMath::Abs(mx2_PPipPim)<0.008){mxP_pass=1.0;}

         if(TMath::Abs(mx2_PPipPim)<0.008){mxP_pass=1.0;}

        else{mxP_pass=0.0;}

        

        mxP_tight_pass=1.0;

        

        if(TMath::Abs(mx_P - M_Rho)<0.06 && TMath::Abs(mx2_PPipPim)<0.005 && TMath::Abs(me_PPipPim - Pgam)<0.1 && Pgam>0.1 && me_PPipPim>0.1){mx2_PPipPimGam_pass=1.0;}

        else{mx2_PPipPimGam_pass=0.0;}

        

        //mx2_PPipPimGam_pass=1.0;

        

        if(TMath::Abs(mx_P - 0.1349766)<0.06){gamEta_pass=1.0;}

        else{gamEta_pass=0.0;}

        

        gamEtaP_pass=1.0;

        gamEtaP_tight_pass=1.0;

        mx2_PPipPim_pass=1.0;

        

        if(beam >= 1.58 && beam <= 3.502){eBeam_pass=1.0;}

        else{eBeam_pass=0.0;}
    
    
    if(runNum != 43490 && runNum != 43491 && runNum != 43492 && runNum != 43493 && runNum != 43494 && runNum != 43495 && runNum != 43496 && runNum != 43497 && runNum != 43498 && runNum != 43499 && runNum != 43500 && runNum != 43501 && runNum != 43502 && runNum != 43503 && runNum != 43504 && runNum != 43505 && runNum != 43506 && runNum != 43507 && runNum != 43508 && runNum != 43509 && runNum != 43510 && runNum != 43511 && runNum != 43512 && runNum != 43513 && runNum != 43514 && runNum != 43515 && runNum != 43516 && runNum != 43517 && runNum != 43518 && runNum != 43519 && runNum != 43520 && runNum != 43521 && runNum != 43522 && runNum != 43523 && runNum != 43524 && runNum != 43525 && runNum != 43558 && runNum != 43675 && runNum != 43676 && runNum != 43777 && runNum != 43778 && runNum != 43871 && runNum != 43981 && runNum != 43982 && runNum != 43989 && runNum != 43990 && runNum != 43991 && runNum != 44013 && runNum != 44108 && runNum != 44109 && runNum != 44110 && runNum != 44111 && runNum != 44112 && runNum != 44113){good_run_pass=1.0;}
    else{good_run_pass=0.0;}
    
    if (mx2_PPipPimGam_pass)
      {
        buffer[0] = Pp;
        buffer[1] = Ppip;
        buffer[2] = Ppim;
        buffer[3] = Pgam;
        buffer[4] = P_theta;
        buffer[5] = Pip_theta;
        buffer[6] = Pim_theta;
        buffer[7] = Gam_theta;
        buffer[8] = P_phi;
        buffer[9] = Pip_phi;
        buffer[10] = Pim_phi;
        buffer[11] = Gam_phi;
        buffer[12] = mx_P;
        buffer[13] = me_PPipPim;
        buffer[14] = beam;
        buffer[15] = cosTheta;
        buffer[16] = gamEta;
        buffer[17] = IM_PipPim;
        buffer[18] = IM_PipPimGam;
        buffer[19] = IM2_PipPim;
        buffer[20] = phi;
        buffer[21] = vz_P;
        buffer[22] = vz_Pip;
        buffer[23] = vz_Pim;
        buffer[24] = runNum;
        buffer[25] = px_G;
        buffer[26] = py_G;
        buffer[27] = pz_G;
        buffer[28] = px_Pip;
        buffer[29] = py_Pip;
        buffer[30] = pz_Pip;
        buffer[31] = px_Pim;
        buffer[32] = py_Pim;
        buffer[33] = pz_Pim;
        buffer[34] = mx2_PPipPimGam;
        buffer[35] = mx_PPipPimGam;
        buffer[36] = px_P;
        buffer[37] = py_P;
        buffer[38] = pz_P;
        buffer[39] = mx2_PPipPim;
        
        T->Fill(buffer);
      }
      
    dataTree->Fill();
    
  }
  
  //###########################################   END OUTGOING DATA  #######################################
  

  dataTree->Write();
  

  T->Write();
  f->Write();
  
  f->Close();
}




