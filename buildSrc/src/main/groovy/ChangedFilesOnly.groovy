import java.util.List;
import java.util.ArrayList;

class ChangedFilesOnly{

   List<String> getChangedFileList(String path){
      List<String> fileList =  new ArrayList<String>();
      path = path.replace("\\","/");
      String changedFilesCommand ="git diff --name-only --diff-filter=ACMRTUBX -- ${path}";
      String newFilesCommand = "git ls-files -o -- ${path}";
      fileList = this.addToList(changedFilesCommand, fileList);
      fileList = this.addToList(newFilesCommand, fileList);
      return fileList;
   }

   private List<String> addToList(String command, List<String> fileList){
      Runtime r = Runtime.getRuntime();
      try
      {
         Process proc             =  r.exec(command);

         proc.waitFor();
         BufferedReader buffer = new BufferedReader(new InputStreamReader(proc.getInputStream()));
         String line = "";

         while ((line = buffer.readLine()) != null) {
            String fileName =  line.substring(line.lastIndexOf('/')+1, line.length());
            fileList.add(fileName);
         }
         
         buffer.close();
         
         return fileList;
      }catch(Exception e){
         System.out.error(e);
      }
   }
}