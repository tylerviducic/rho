import org.jlab.jnp.utils.file.FileUtils

//for(String f: FileFinder.findTheseFiles("/home/tylerviducic/research/testDirForGagik/test*")){
//    println(f);
//}

for(String f : FileFinder.getFiles("/home/tylerviducic/research/testDirForGagik", "test*")){
    println(f);
}

public class FileFinder {

    public static List<String> listOfFiles = new ArrayList<String>();
    private static String newKeyWord = "";


    public static List<String> getFiles(String directory, String wildcard) {
        String newDir = "";
        if (!directory[directory.length() - 1].equals("/")) {
            newDir = directory + "/";
        } else {
            newDir = directory;
        }
        List<String> filesInDirectory = FileUtils.getFileListInDir(directory);

        if (wildcard.contains("*")) {
            newKeyWord = newDir + wildcard.replace("*", ".*");
        } else {
            newKeyWord = newDir + wildcard;
        }
        for (String f : filesInDirectory) {
            if (f.matches(newKeyWord)) {
                listOfFiles.add(f.toString());
            }
        }
        return this.listOfFiles;
    }

    public static List<String> getDirectoryName(String fullPath) {
        List<String> dirFile = new ArrayList<String>();
        int start = fullPath.lastIndexOf("/");
        dirFile.add(0, fullPath.substring(0, start + 1));
        dirFile.add(1, fullPath.substring(start + 1));
        println(dirFile);
        return dirFile;
    }

    public static List<String> getFiles(String fullPath) {
        List<String> dirCombo = getDirectoryName(fullPath);
        return getFiles(dirCombo.get(0), dirCombo.get(1));
    }

}

//    public static List<String> findTheseFiles(String pathToFiles){
//        List<String> listOfFiles = new ArrayList<String>();
//        String directory="";
//        String newKeyWord="";
//        //pathToFiles.lastIndexOf("/");
//        String[] dirs = pathToFiles.split("/");
//
//        for(int i = 0; i < dirs.length-1; i++){
//            directory = directory+dirs[i]+"/";
//        }
//        String keyWord = dirs[dirs.length-1];
//        if(keyWord.contains("*")){
//            newKeyWord = directory + keyWord.replace("*", ".*");
//        }else{newKeyWord = directory + keyWord;}
//
//        List<String> filesInDirectory= FileUtils.getFileListInDir(directory);
//        for(String f : filesInDirectory){
//            if(f.matches(newKeyWord)){
//                listOfFiles.add(f);
//            }
//        }
//        return listOfFiles;
//    }



