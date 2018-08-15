//  code to produce plots for thesis in data analysis section
//  and to do systematic error analysis
//  Created by Torri Roark on 7/20/18.
//

#include <stdio.h>

void histosData(){
    
    cout << "check" << endl;
    //put file with dataTree here, i.e. the output from Rho.CC code or whatever you called it
    TFile *f = new TFile("/home/physics/research/rho/g11/PipPimRHo.root");
    TTree *G = (TTree*)f->Get("dataTree");
    
    //declaring variables
    Double_t beam, me_PPipPim, mx2_PPipPim, mx2_PPipPimGam, cosTheta;
    
    Double_t IM_PipPim,IM_PipPimGam,IM2_PipPim,mx_P;
    
    Double_t Pp, Ppip, Ppim, Pgam;
    
    Double_t P_theta, Pip_theta, Pim_theta, Gam_theta;
    
    Double_t P_phi, Pip_phi, Pim_phi, Gam_phi;
    
    Int_t charge, neutral;
    
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
    
    cout << "check 2" << endl;
    
    //rename output file here
    TFile *z = new TFile("/home/physics/research/rho/g11/sidebands.root","RECREATE");
    
    cout << "check 3" << endl;
    
    //creating arrays to store histgrams and name them
    TH1F *mxp[61];
    TH1F *mx2_sig[61];
    TH1F *mx2_sb_left[61];
    TH1F *mx2_sb_right[61];
    TH1F *subtract[61];
    char hname[61];
    char cname[61];
    char sbname_right[61];
    char sbname_left[61];
    char sub[61];
    char bin[61];
    char bin_sig[61];
    char bin_sb_left[61];
    char bin_sb_right[61];
    char bin_sub[61];
    double e;
    for (int k=0; k<=61; k++)
    {
        e=0.3+(double(k)/100.0); //connecting histogram number with bin number
     
         sprintf(hname,"h%d",k);
         sprintf(bin, "mx_P in region of rho for abs(IM_PipPim - %.2f) < 0.005",e);
         mxp[k] = new TH1F(hname, " " ,100,0.6,0.9);
     
        sprintf(cname,"signal%d",k);
        sprintf(bin_sig, " abs(IM_PipPim - %.2f) < 0.005",e);
        mx2_sig[k] = new TH1F(cname, bin_sig, 80, -0.05,0.05);
        
        sprintf(sbname_left,"sideband%d left",k);
        sprintf(bin_sb_left, "abs(IM_PipPim - %.2f) < 0.005",e);
        mx2_sb_left[k] = new TH1F(sbname_left, bin_sb_left, 80, -0.05,0.05);
        
        sprintf(sbname_right,"sideband%d right",k);
        sprintf(bin_sb_right, "abs(IM_PipPim - %.2f) < 0.005",e);
        mx2_sb_right[k] = new TH1F(sbname_right, bin_sb_right, 80, -0.05,0.05);
    /*
        sprintf(sub,"subtract%d",k);
        sprintf(bin_sub, "subtracted, abs(IM_PipPim-%.2f) < 0.005",e);
        subtract[k] = new TH1F(sub, bin_sub, 80, -0.05,0.05);
        */
    }
    
    Double_t nEvnt=G->GetEntries();
    
    cout << "check 5" << endl;
    
    for (Int_t i=0; i<nEvnt; i++)
    {
        G->GetEntry(i);
       
        Int_t k=0;
        double j=0.3;
        double h;
        for (int k=0; k<=60; k++)
        {
            h=0.3+(double(k)/100.0);
            //signal 0.650-0.890, 240 MeV wide 
            if (abs(mx_P-M_Rho)<0.12 && me_PPipPim > 0.1 && abs(mx2_PPipPimGam) < 0.002 && abs(me_PPipPim-Pgam)<0.05 && Pgam > 0.1)
            {
                if (abs(IM_PipPim-h)<0.005)
                {
                    mx2_sig[k]->Fill(mx2_PPipPim);
                }
            }
        
            
            //sidebands need to each be 40 MeV wide (i.e. half the width of signal region) || change for rho
            if (me_PPipPim > 0.1  && abs(me_PPipPim - Pgam)<0.05 && abs(mx2_PPipPimGam)<0.002  && abs(mx_P-0.915)<0.015)
            {
                if (abs(IM_PipPim-h)<0.005)
                {
                    mx2_sb_right[k]->Fill(mx2_PPipPim);
                }
            }
        
         
            if (me_PPipPim > 0.1  && abs(me_PPipPim - Pgam)<0.05 && abs(mx2_PPipPimGam)<0.002 && abs(mx_P-0.625)<0.015)
            {
                if (abs(IM_PipPim-h)<0.005)
                {
                    mx2_sb_left[k]->Fill(mx2_PPipPim);
                }
            }
        }
    }
    
    cout <<"check 4" << endl;
    
    z->Write();
    z->Close();
    
}

