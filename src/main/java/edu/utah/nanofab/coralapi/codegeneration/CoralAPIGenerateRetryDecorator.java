package edu.utah.nanofab.coralapi.codegeneration;


import edu.utah.nanofab.coralapi.CoralAPIInterface;
import java.util.Iterator;
import java.util.List;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.Importer;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodHolderSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.ParameterSource;
import org.jboss.forge.roaster.model.source.PropertySource;

/**
 * Design patterns from GoF
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class CoralAPIGenerateRetryDecorator
{
   private CoralAPIGenerateRetryDecorator() {}

   /**
    * Creates a class based on the Decorator design pattern.
    * 
    * @param javaClass the {@link JavaClassSource} for which the decorator will be created
    * @return the Decorator class.
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public static JavaClassSource createDecorator(JavaType<?> javaSource)
   {
      // Create Decorator Class
      String decoratorClassName = javaSource.getName() + "RetryDecorator";
      JavaClassSource decoratorClass = Roaster.create(JavaClassSource.class)
               .setName(decoratorClassName)
               .setPackage(javaSource.getPackage())
               .addInterface(CoralAPIInterface.class);
      if (javaSource instanceof Importer)
      {
         List<Import> imports = ((Importer) javaSource).getImports();
         for (Import i : imports)
         {
            decoratorClass.addImport(i);
         }
      }
      MethodSource<JavaClassSource> constructor = decoratorClass.addMethod()
               .setPublic().setConstructor(true);
      constructor.addParameter(javaSource, "delegate");
      constructor.setBody("this.delegate = delegate;\n"
              + "this.numberOfRetries = 2;\n");
      
      // Create the Delegate final field
      decoratorClass.addField().setPrivate().setFinal(true)
               .setType(javaSource).setName("delegate");

      decoratorClass.addField().setPrivate().setFinal(true)
               .setType(Integer.class).setName("numberOfRetries");

      if (javaSource instanceof MethodHolderSource)
      {
         List<MethodSource<?>> methods = ((MethodHolderSource) javaSource)
                  .getMethods();
         for (MethodSource method : methods)
         {
            if (method.isPrivate())
               continue;
            if (method.isConstructor())
                continue;
            MethodSource<JavaClassSource> decoratorMethod = decoratorClass
                     .addMethod().setPublic().setName(method.getName());
            StringBuilder sb = new StringBuilder();
            if (method.isReturnTypeVoid())
            {
               decoratorMethod.setReturnTypeVoid();
               sb.append("delegate.");
            }
            else
            {
               sb.append("returnValue = delegate.");
               decoratorMethod.setReturnType(method.getReturnType()
                        .getQualifiedNameWithGenerics());
            }
            sb.append(method.getName()).append("(");

            List<ParameterSource<?>> parameters = method.getParameters();
            for (Iterator<ParameterSource<?>> iterator = parameters
                     .iterator(); iterator.hasNext();)
            {
               ParameterSource<JavaClassSource> param = (ParameterSource<JavaClassSource>) iterator
                        .next();
               sb.append(param.getName());
               if (iterator.hasNext())
               {
                  sb.append(",");
               }
               
               decoratorMethod.addParameter(param.getType()
                        .getQualifiedNameWithGenerics(), param.getName());
            }
            sb.append(")");
            for (String o : (List<String>) method.getThrownExceptions())
            {
               decoratorMethod.addThrows(o);
            }
            String body = outerTemplate(sb.toString(), (List<String>) method.getThrownExceptions(), method);
            decoratorMethod.setBody(body);
         }
      }
      return decoratorClass;
   }
   
    public static String outerTemplate(String functionCall, List<String> exceptions, MethodSource method) {
        String template = "" +
"        int count = 0;\n" +
"        boolean success = false;\n" +
"        Exception caughtException = null;\n";
        if (!method.isReturnTypeVoid()) {
            String type = method.getReturnType().getQualifiedNameWithGenerics();
            if (type.equals("boolean")) {
                template += "boolean returnValue = false;\n";
            } else {
                template += type + " returnValue = null;\n";
            }
        }
        template +=
"        while (count < numberOfRetries && !success) {\n" +
"            count++;\n" +
"            try {\n" +
"                " + functionCall + ";\n" +
"                success = true;\n" +
"            }" +
"            " + innerTemplate(exceptions) + "\n" +
"        }\n";
        if (!method.isReturnTypeVoid()) {
            template +=
"        return returnValue;\n";
        }
        //System.out.println(template);
        return template;
    }
    
    public static String innerTemplate(List<String> exceptions) {
        String buffer = "";
        int count = 1 ;
        boolean containsException = true;
        if (!exceptions.contains("Exception")) {
            exceptions.add("Exception");
            containsException = false;
        }
        for(String e : exceptions) {
            buffer += "" +
"             catch (" + e + " ex" + count + ") {\n" +
"                caughtException = ex" + count + ";\n" +
"                if (count >= numberOfRetries) {\n";
            if (!containsException && e.equals("Exception")) {
                //do nothing;
            } else {
                buffer += 
"                    throw ex" + count + ";\n";
            }
            buffer +=
"                }\n" +
"                //log this\n" +
"                try {\n" +
"                    this.delegate.reInitialize();\n" +
"                } catch (Exception innerEx) {\n" +
"                    //ignore\n" +
"                }\n" +
"            }";
            count++;
        }
        return buffer;
    }
   

}