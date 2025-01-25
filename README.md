# 3DViewer ![](https://github.com/andreydem0505/3DViewer/workflows/Testing/badge.svg)

![](https://raw.githubusercontent.com/andreydem0505/3DViewer/refs/heads/main/image.png)

Final project for computer graphics course in Voronezh State University

3D models render written in Java allows user to interact with models and even create animations.

[Watch how it works!](https://youtu.be/lfBWsFlg0Xo)

## Contributors

[Vladislav Severov](https://github.com/lein3000zzz): UI/UX, integration, loading and saving models and animations,
multiple models support, removing model's vertices and polygons, exceptions handling, exe file, qol functionality and music :D.

[Ilya Ektov](https://github.com/LiptonItTea): math module, affine transformations, camera control, 
serializing and deserializing animations.

[Andrey Dementiev](https://github.com/andreydem0505): model rasterization, texture and lighting, multiple 
cameras support, animations core.

## Features

User can choose a way of displaying model. There are different options:

1. Showing polygon grid or not
2. Filling model with a plain color or a texture
3. Using lighting or not

User has ability to add several models and work with them simultaneously. He can perform affine transformations on them:
translate, rotate and scale models. Also, he can manage several cameras to see the model from different sides. Camera 
control is performing with the help of polar coordinates.

Any state of the current model can be set as an animation frame allowing user to compose diverse animations. Then they 
can be saved as json files and loaded to the program.

Developing this project required knowledge of linear algebra and computer graphics concepts (rasterization, 
interpolation, barycentric coordinates, z-buffer, lighting) as well as OOP and code architecture.

The code is covered by unit-tests.

## Build
Building is performed via maven.
