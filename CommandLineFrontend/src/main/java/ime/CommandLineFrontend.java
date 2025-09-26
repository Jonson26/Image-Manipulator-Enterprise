package ime;

import java.io.File;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.example.jamroga.ime.api.MenuElement;
import com.example.jamroga.ime.api.OutputContainer;
import com.example.jamroga.ime.api.ProcessorService;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@ComponentScan(basePackages = {"com.example.jamroga.ime.api"})
@Component
@Slf4j
@CommandLine.Command(name = "ime",
    description = "Image Manipulator Enterprise")
public class CommandLineFrontend implements Callable<Integer> {
    @Autowired
    ProcessorService processorService;

    @CommandLine.Option(names = {"-F", "--file"})
    private File file;
    
    @CommandLine.Option(names = {"-O", "--output"})
    private File output;
    
    @CommandLine.Option(names = {"-E", "--effect"})
    private String effect;
    
    @CommandLine.Option(names = {"-P", "--process"})
    private String process;
    
    @CommandLine.Option(names = {"-L", "--list"})
    private boolean list;
    
    @CommandLine.Option(names = {"-h", "--help"})
    private boolean help;

    @Override
    public Integer call() throws Exception {
        if(list){
            System.out.println("Available effects:");
            for(MenuElement me : processorService.getPixelProcessorMenuElements()){
                System.out.printf("%s : %s %n", me.name(), me.description());
            }
            System.out.println("Available processes:");
            for(MenuElement me : processorService.getImageProcessorMenuElements()){
                System.out.printf("%s : %s %n", me.name(), me.description());
            }
        }
        if(help){
            CommandLine.usage(new CommandLineFrontend(), System.out);
        }
        if(file != null) {
            log.info("FILE : {}", file);
            if(effect == null) effect = processorService.getPixelProcessorMenuElements().getFirst().name();
            if(process == null) process = processorService.getImageProcessorMenuElements().getFirst().name();
            if(output == null) output = new File("./output.png");
            
            System.out.println("Processing Image");
            
            int outIndex = processorService.processImage(
                "", 
                file.getPath(), 
                effect, 
                process);
            
            OutputContainer out = processorService.getConvertedImage(outIndex);

            System.out.printf("Waiting for processing of %s with %s using %s to finish", file.getPath(), effect, process);
            
            while(!out.isFinished()){
                Thread.sleep(100);
            }
            
            System.out.printf("Writing processed image to %s", output.getPath());
            
            ImageIO.write(out.getImage(), "png", output);
        }

        System.exit(0);
        return 0;
    }
}
