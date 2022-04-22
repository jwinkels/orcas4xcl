import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.eclipse.emf.common.util.EList;

import de.opitzconsulting.orcasDsl.*;
import de.opitzconsulting.orcasDsl.impl.*;
import de.opitzconsulting.orcas.diff.*;

class AutomaticIndex implements UnaryOperator{
   List<String> toIndexKey( String pTablename, EList<ColumnRef> pIndexColumns )
   {
      List<String> lReturn = new ArrayList<String>();

      String lIndexKey = pTablename +
                        " cols ";

      for( ColumnRef lColumnRef : pIndexColumns )
      {
      lIndexKey += lColumnRef.getColumn_name() +
                     " ";

      lReturn.add( lIndexKey );
      }

      return lReturn;
   }

   List<ColumnRef> copyColumnRefs( Iterable<ColumnRef> pColumnRefs )
   {
      List<ColumnRef> lReturn = new ArrayList<ColumnRef>();

      for( ColumnRef lColumnRef : pColumnRefs )
      {
         ColumnRef lCopyColumnRef = new ColumnRefImpl();

         lCopyColumnRef.setColumn_name( lColumnRef.getColumn_name() );

         lReturn.add( lCopyColumnRef );
      }

      return lReturn;
   }

   Object apply(Object pModel){


      String lIndexPostfix = "_GEN_IX";
      Set<String> lIndexSet = new HashSet<String>();

      for( ModelElement lModelElement : pModel.getModel_elements() )
      {
         if( lModelElement instanceof Table )
         {
         Table lTable = (Table)lModelElement;

         if( lTable.getPrimary_key() != null )
         {
            lIndexSet.addAll( toIndexKey( lTable.getName(), lTable.getPrimary_key().getPk_columns() ) );
         }

         for( IndexOrUniqueKey lIndexOrUniqueKey : lTable.getInd_uks() )
         {
            if( lIndexOrUniqueKey instanceof Index )
            {
               lIndexSet.addAll( toIndexKey( lTable.getName(), ((Index)lIndexOrUniqueKey).getIndex_columns() ) );
            }
            if( lIndexOrUniqueKey instanceof UniqueKey )
            {
               lIndexSet.addAll( toIndexKey( lTable.getName(), ((UniqueKey)lIndexOrUniqueKey).getUk_columns() ) );
            }
         }
         }
         if( lModelElement instanceof IndexExTable )
         {
         lIndexSet.addAll( toIndexKey( ((IndexExTable)lModelElement).getTable_name(), ((IndexExTable)lModelElement).getIndex_columns() ) );
         }
      }

      for( ModelElement lModelElement : pModel.getModel_elements() )
      {
         if( lModelElement instanceof Table )
         {
         Table lTable = (Table)lModelElement;

         for( ForeignKey lForeignKey : ((Table)lModelElement).getForeign_keys() )
         {
            List<String> lIndexKeys = toIndexKey( lTable.getName(), lForeignKey.getSrcColumns() );

            if( !lIndexSet.containsAll( lIndexKeys ) )
            {
               Index lIndex = new IndexImpl();

               lIndex.setConsName( lForeignKey.getConsName() +
                                    lIndexPostfix );

               lIndex.getIndex_columns().addAll( copyColumnRefs( lForeignKey.getSrcColumns() ) );

               lTable.getInd_uks().add( lIndex );

               lIndexSet.addAll( toIndexKey( lTable.getName(), lIndex.getIndex_columns() ) );
            }
         }
         }
      }

      return pModel;
   }
}