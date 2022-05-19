import java.util.List;
import java.util.ArrayList;

class ChangedFilesOnly{

   List<String> getChangedFileList(String path, String srcDir){
      List<String> fileList =  new ArrayList<String>();
      path = path.replace("\\","/");
      String changedFilesCommand ="git diff --name-only --diff-filter=ACMRTUBX -- ${path}";
      String newFilesCommand = "git ls-files -o -- ${path}";
      fileList = this.addToList(changedFilesCommand, fileList, srcDir);
      fileList = this.addToList(newFilesCommand, fileList, srcDir);
      return fileList;
   }

   private List<String> addToList(String command, List<String> fileList, String srcDir){
      Runtime r = Runtime.getRuntime();
      try
      {
         Process proc             =  r.exec(command);

         proc.waitFor();
         BufferedReader buffer = new BufferedReader(new InputStreamReader(proc.getInputStream()));
         String line = "";

         while ((line = buffer.readLine()) != null) {
            String fileName =  line.substring(line.lastIndexOf('/')+1, line.length());
            fileList.add(srcDir + '/' + fileName);
         }
         
         buffer.close();
         
         return fileList;
      }catch(Exception e){
         System.out.error(e);
         buffer.close();
      }
   }
}