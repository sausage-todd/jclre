package jclre.tool;

import javassist.*;
import jclre.cache.FieldsCache;
import jclre.cache.OriginalBytecodeCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class JclreClassRedefiner {

    private static final Logger log = LoggerFactory.getLogger( JclreClassRedefiner.class );

    private ClassPool classPool;
    private OriginalBytecodeCache originalBytecodeCache;
    private Instrumentation instrumentation;
    private FieldsCache fieldsCache;

    public JclreClassRedefiner(
        ClassPool classPool,
        OriginalBytecodeCache originalBytecodeCache,
        Instrumentation instrumentation,
        FieldsCache fieldsCache
    ) {
        this.classPool = classPool;
        this.originalBytecodeCache = originalBytecodeCache;
        this.instrumentation = instrumentation;
        this.fieldsCache = fieldsCache;
    }

    public void redefine( Class clazz ) {
        try {
            log.info( "redefining " + clazz.getName() );
            CtClass newClass = classPool.get( clazz.getName() );
            newClass.detach();
//            String className = newClass.getName();
//
//            CtClass oldClass = classPool.makeClass( new ByteArrayInputStream( originalBytecodeCache.get( className ) ) );
//
//            for( CtField f : newClass.getDeclaredFields() ) {
//                if( f.getName().equals( "___ADDED_FIELDS___" ) ) {
//                    continue;
//                }
//
//                try {
//                    oldClass.getDeclaredField( f.getName() );
//                } catch( NotFoundException e ) {
//                    log.info( "added field " + f.getType().getName() + " " + f.getName() );
//                    newClass.removeField( f );
//                    fieldsCache.addField(
//                        className,
//                        new FieldsCache.FieldDefinition(
//                            f.getName(),
//                            null,
//                            null
//                        )
//                    );
//                }
//            }
//
            instrumentation.redefineClasses(
                new ClassDefinition(
                    clazz,
                    newClass.toBytecode()
                )
            );
//            log.info( "redefined" );
        } catch( ClassNotFoundException e ) {
            log.error( "class not found " + clazz.getName(), e );
        } catch( CannotCompileException e ) {
            log.error( "can't compile " + clazz.getName(), e );
        } catch( UnmodifiableClassException e ) {
            log.error( "unmodifiable class " + clazz.getName(), e );
        } catch( NotFoundException e ) {
            log.error( "not found " + clazz.getName(), e );
        } catch( IOException e ) {
            log.error( "io " + clazz.getName(), e );
        } catch( RuntimeException e ) {
            log.error( "runtime " + clazz.getName(), e );
        }
    }

}