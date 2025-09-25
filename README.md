# Image Manipulator Enterpirse

Image Manipulator Enterprise is a simple image processing application written 
in Java that makes use of Spring Boot. It is meant to showcase my understanding 
of various programming technologies and best practices, rather than provide any 
sort of commercially viable product. Despite this, the application can still be 
used to generate some fun visual effects!

## Features

The application consists of a graphics processing backend and two different 
frontends, those being:
 * A web-based graphical user interface
 * A command-line interface for running the application on single files

The backend provides a selection of ways to parallelize the conversion, as 
well as several different graphical effects to choose from.

### Effects

#### Base image used for demonstration
![Unprocessed image](/readme_assets/RGB_24bits_palette_sample_image.jpg)

#### Blur simple average
This effect simply calculates the average of a 9 by 9 area around each given 
pixel to achieve a simple blur effect.

![Blurred image](/readme_assets/Blur%20Image-RGB_24bits_palette_sample_image.png)

#### Magenta deep fry
An effect I made during the initial prototyping stage that I quite liked. It
follows the very simple formula of `newColour = (oldColour+magenta)/2`.

![Deep fried image](/readme_assets/Magenta%20Deep%20Fry-RGB_24bits_palette_sample_image.png)

#### Map to 16 colour palette
Uses Bayer dithering & nearest neighbour to map an image to the Mac System 4.1
16 colour palette.

![16 colour image](/readme_assets/Map%20image%20to%2016%20colour%20palette-RGB_24bits_palette_sample_image.png)

#### Map to 16 shades of grey palette
This is very similar to the previous effect, except it maps the image to a 16 
shades of grey palette.

![16 shades of grey image](/readme_assets/Map%20image%20to%2016%20shades%20of%20gray%20palette-RGB_24bits_palette_sample_image.png)

#### Map to 16 most common colours
This effect first calculates a custom palette based on the 16 most common 
colours found in the provided image. Afterward it exactly operates like the 
[Map to 16 colour palette](#map-to-16-colour-palette) effect.

![16 most common colours image](/readme_assets/Map%20image%20to%2016%20most%20common%20colours-RGB_24bits_palette_sample_image.png)
