#version 150

#moj_import <fog.glsl>

in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec4 texProj0;
out float vertexDistance;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texProj0 = gl_Position;
    vertexDistance = fog_distance(ModelViewMat, Position, FogShape);
}
