import java.util.function.UnaryOperator;
import de.opitzconsulting.orcasDsl.*;
import de.opitzconsulting.orcasDsl.impl.*;
import de.opitzconsulting.orcas.diff.*;

class UpperCasePKTablespace implements UnaryOperator{

   Object apply(Object pModel){

      for( ModelElement lModelElement : pModel.getModel_elements() )
      {
         if( lModelElement instanceof Table )
         {
            Table lTable = (Table)lModelElement;

            if( lTable.getPrimary_key() != null )
            {
               if(lTable.getPrimary_key().getTablespace() != null){
                  lTable.getPrimary_key().setTablespace(lTable.getPrimary_key().getTablespace().toUpperCase())
               }
            }

         }
      }

      return pModel;
   }
}