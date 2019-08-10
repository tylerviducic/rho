import org.jlab.jnp.hipo4.io.HipoReader
import java.io.FileNotFoundException

String dataFile = "/lustre/expphy/volatile/clas12/rg-a/production/recon/pass1/dst/v2/005163/dst_clas_005163.evio.00315-00319.hipo";

HipoReader reader = new HipoReader();
try {
    reader.open(dataFile);
} catch (FileNotFoundException e){
    println("Exception caught.");
}
