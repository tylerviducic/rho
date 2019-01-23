void Plot_Data()
{
    gStyle->SetOptStat(11111111);
    
    TFile *f1=new TFile("/home/physics/research/rho/g11/PipPimRHo.root");
    
    TTree *G = (TTree*) f1->Get("dataTree");
    
    Double_t beam, me_PPipPim, mx2_PPipPim, mx2_PPipPimGam, cosTheta;
    
    Double_t IM_PipPim,IM_PipPimGam,IM2_PipPim,mx_P;
    
    Double_t Pp, Ppip, Ppim, Pgam;
    
    Double_t P_theta, Pip_theta, Pim_theta, Gam_theta;
    
    Double_t P_phi, Pip_phi, Pim_phi, Gam_phi;
    
    Double_t M_EtaP = 0.95778;
    Double_t M_Eta = 0.547853;
    Double_t M_Rho = 0.770;

    G->SetBranchAddress("beam",&beam);
    G->SetBranchAddress("me_PPipPim",&me_PPipPim);
    G->SetBranchAddress("mx2_PPipPimGam",&mx2_PPipPimGam);
    G->SetBranchAddress("mx2_PPipPim",&mx2_PPipPim);
    G->SetBranchAddress("cosTheta",&cosTheta);
    
    G->SetBranchAddress("IM_PipPim",&IM_PipPim);
    G->SetBranchAddress("IM_PipPimGam",&IM_PipPimGam);
    G->SetBranchAddress("IM2_PipPim",&IM2_PipPim);
    G->SetBranchAddress("mx_P",&mx_P);
    
    G->SetBranchAddress("Pp",&Pp);
    G->SetBranchAddress("Ppip",&Ppip);
    G->SetBranchAddress("Ppim",&Ppim);
    G->SetBranchAddress("Pgam",&Pgam);
    
    G->SetBranchAddress("P_theta",&P_theta);
    G->SetBranchAddress("Pip_theta",&Pip_theta);
    G->SetBranchAddress("Pim_theta",&Pim_theta);
    G->SetBranchAddress("Gam_theta",&Gam_theta);
    
    G->SetBranchAddress("P_phi",&P_phi);
    G->SetBranchAddress("Pip_phi",&Pip_phi);
    G->SetBranchAddress("Pim_phi",&Pim_phi);
    G->SetBranchAddress("Gam_phi",&Gam_phi);

    TH1D* h_beam = new TH1D("h_beam","Beam [GeV]",200,1.51,3.51);
    
    TH1D* h_me = new TH1D("h_me","me_PPipPim [GeV]",200,0,1);
    TH1D* h_mePg = new TH1D("h_mePg","(me_PPipPim-Pgam) [GeV]",40,-0.2,0.5);
    TH1D* h_mx2_PPipPim = new TH1D("h_mx2_PPipPim","mx2_PPipPim [GeV^{2}]",100,-0.05,0.05);
    TH1D* h_mx2_PPipPimGam = new TH1D("h_mx2_PPipPimGam","mx2_PPipPimGam [GeV^{2}]",200,-0.001,0.001);
    TH1D* h_mPipPimG = new TH1D("h_mPipPimG","mPipPimGam [GeV]",30,0.82,1.1);
    
    TH1D* h_mx_P = new TH1D("h_mx_P","mx_P [GeV]",30,0.892,1.012);
    
    TH1D* h_Pp = new TH1D("h_Pp","Proton Momentum [GeV]",200,0,2.5);
    TH1D* h_Ppip = new TH1D("h_Ppip","#pi^{+} Momentum [GeV]",200,0,2.5);
    TH1D* h_Ppim = new TH1D("h_Ppim","#pi^{-} Momentum [GeV]",200,0,2.5);
    TH1D* h_Pgam = new TH1D("h_Pgam","#gamma Momentum [GeV]",200,0,2.5);
    
    TH1D* h_th_P = new TH1D("h_th_P","#theta_{P} [Degrees]",200,0,60);
    TH1D* h_th_Pip = new TH1D("h_th_Pip","#theta_{#pi^{+}} [Degrees]",200,0,140);
    TH1D* h_th_Pim = new TH1D("h_th_Pim","#theta_{#pi^{-}} [Degrees]",200,0,140);
    TH1D* h_th_G = new TH1D("h_th_G","#theta_{#gamma} [Degrees]",200,0,60);
    
    TH1D* h_phi_P = new TH1D("h_phi_P","#phi_{P} [Degrees]",200,-180,180);
    TH1D* h_phi_Pip = new TH1D("h_phi_Pip","#phi_{#pi^{+}} [Degrees]",200,-180,180);
    TH1D* h_phi_Pim = new TH1D("h_phi_Pim","#phi_{#pi^{-}} [Degrees]",200,-180,180);
    TH1D* h_phi_G = new TH1D("h_phi_G","#phi_{#gamma} [Degrees]",200,-180,180);

    //    TH2D* h2_Mpipi_MxP = new TH2D("h2_Mpipi_MxP","",6,0.32,0.92,25,0.9,1); //for data_histos.root
    
    TH2D* h2_Mpipi_MxP = new TH2D("h2_Mpipi_MxP","",6,0.32,0.92,48,0.925,0.985); //for data_histos_1.root

    TH1D* h_mx_P_mpipiRange = new TH1D("h_mx_P_mpipiRange","mx_P [GeV]",30,0.892,1.012);

    Double_t nEvnt=G->GetEntries();
    
    for( Int_t i = 0; i < nEvnt; i++)
    {
        G->GetEntry(i);
        
        h_beam->Fill(beam);
        
        if(TMath::Abs(mx_P - M_Rho)<0.06 && TMath::Abs(me_PPipPim - Pgam)<0.1 && Pgam>0.1 && TMath::Abs(mx2_PPipPimGam)<0.005 && TMath::Abs(mx2_PPipPim)<0.005)
        {h_me->Fill(me_PPipPim);}
        
        if(TMath::Abs(mx_P - M_Rho)<0.06 && me_PPipPim>0.1 && Pgam>0.1 && TMath::Abs(mx2_PPipPimGam)<0.0005 && TMath::Abs(mx2_PPipPim)<0.005)
        {h_mePg->Fill(me_PPipPim-Pgam);}
        
        if(TMath::Abs(mx_P - M_Rho)<0.06 && me_PPipPim - Pgam>-0.1 && me_PPipPim - Pgam<0.1 && me_PPipPim>0.1 && Pgam>0.1 && TMath::Abs(mx2_PPipPimGam)<0.0005)
        {h_mx2_PPipPim->Fill(mx2_PPipPim);}
        
        if(TMath::Abs(mx_P - M_Rho)<0.06 && me_PPipPim - Pgam>-0.1 && me_PPipPim - Pgam< 0.1 && me_PPipPim>0.1 && Pgam>0.1 && TMath::Abs(mx2_PPipPim)<0.005)
        {h_mx2_PPipPimGam->Fill(mx2_PPipPimGam);}
        
        if(TMath::Abs(mx_P - M_Eta)<0.03 && TMath::Abs(me_PPipPim - Pgam)<0.12 && me_PPipPim>0.1 && Pgam>0.1 && TMath::Abs(mx2_PPipPimGam)<0.0005 && TMath::Abs(mx2_PPipPim)<0.008)
        {h_mPipPimG->Fill(IM_PipPimGam);}
        
            h_mx_P->Fill(mx_P);
            
            h_Pp->Fill(Pp);
            h_Ppip->Fill(Ppip);
            h_Ppim->Fill(Ppim);
            h_Pgam->Fill(Pgam);
            
            h_th_P->Fill(P_theta);
            h_th_Pip->Fill(Pip_theta);
            h_th_Pim->Fill(Pim_theta);
            h_th_G->Fill(Gam_theta);
            
            h_phi_P->Fill(P_phi);
            h_phi_Pip->Fill(Pip_phi);
            h_phi_Pim->Fill(Pim_phi);
            h_phi_G->Fill(Gam_phi);
            
        }

    
    TFile *f=new TFile("data_histos.root","RECREATE");
    h_beam->Write();
    h_me->Write();
    h_mePg->Write();
    h_mx2_PPipPim->Write();
    h_mx2_PPipPimGam->Write();
    h_mPipPimG->Write();
    h_mx_P->Write();
    
    h_Pp->Write();
    h_Ppip->Write();
    h_Ppim->Write();
    h_Pgam->Write();
    
    h_th_P->Write();
    h_th_Pip->Write();
    h_th_Pim->Write();
    h_th_G->Write();
    
    h_phi_P->Write();
    h_phi_Pip->Write();
    h_phi_Pim->Write();
    h_phi_G->Write();
    f->Write();
    f->Close();

 }
