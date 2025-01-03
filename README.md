# 3DViewer ![](https://github.com/andreydem0505/3DViewer/workflows/Testing/badge.svg)
Final project for computer graphics course in Voronezh State University

3D models render written in Java allows user to interact with models and even create animations.

<a href="https://youtu.be/lfBWsFlg0Xo">Watch how it works!</a>

<a href="https://github.com/lein3000zzz">Vladislav Severov<a/>: UI/UX, loading and saving models and 
animations, multiple models support, removing model's vertices, exceptions handling, exe file

<a href="https://github.com/LiptonItTea">Ilya Ektov<a/>: math module, affine transformations, camera control, 
serializing and deserializing animations

<a href="https://github.com/andreydem0505">Andrey Dementiev<a/>: model rasterization, texture and lighting, multiple 
cameras support, animations core

User can choose a way of displaying model. There are different options:
<ol>
<li>Showing polygon grid or not</li>
<li>Filling model with a plain color or a texture</li>
<li>Using lighting or not</li>
</ol>

User has ability to add several models and work with them simultaneously. He can perform affine transformations on them:
translate, rotate and scale models. Also, he can manage several cameras to see the model from different sides. Camera 
control is performing with the help of polar coordinates.

Any state of the current model can be set as an animation frame allowing user to compose diverse animations. Then they 
can be saved as json files and loaded to the program.

Developing this project required knowledge of linear algebra and computer graphics concepts (rasterization, 
interpolation, barycentric coordinates, z-buffer, lighting) as well as OOP and code architecture.

The code is covered by unit-tests.