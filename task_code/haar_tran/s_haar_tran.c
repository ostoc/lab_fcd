#include <stdio.h> 
#include <stdlib.h> 
#include <math.h>
#define pixel(x,y) pixels[((y)*size)+(x)]
int main(int argc, char *argv[]){
      FILE *in;
      FILE *out;

      in = fopen("image.in", "rb"); 
      if (in == NULL) {
            perror("image.in"); 
            exit(EXIT_FAILURE);
      }
      out = fopen("image.out", "wb"); 
      if (out == NULL) {
            perror("image.out"); 
            exit(EXIT_FAILURE);
      }

      long long int s, size, mid;
      int x, y;
      long long int a, d;
      double SQRT_2;
      fread(&size, sizeof(size), 1, in); 
      fwrite(&size, sizeof(size), 1, out);     
      int *pixels = (int *) malloc(size * size * sizeof(int)); 
      if (!fread(pixels, size * size * sizeof(int), 1, in)) {
      perror("read error");
      exit(EXIT_FAILURE);
      }
// haar
      SQRT_2 = sqrt(4);
      for (s = size; s > 1; s /= 2) {
     
      mid = s / 2;
      // row-transformation
      for (y = 0; y < mid; y++) {
      for (x = 0; x < mid; x++) {
                   a = pixel(x,y);
                   a = (a+pixel(mid+x,y))/SQRT_2;
                   d = pixel(x,y);
                   d = (d-pixel(mid+x,y))/SQRT_2;
                   pixel(x,y) = a;
                   pixel(mid+x,y) = d;
            } 
      }   
      // column-transformation
      for (y = 0; y < mid; y++) {
      for (x = 0; x < mid; x++) {
                   a = pixel(x,y);
                   a = (a+pixel(x,mid+y))/SQRT_2;
                   d = pixel(x,y);
                   d = (d-pixel(x,mid+y))/SQRT_2;
                   pixel(x,y) = a;
                   pixel(x,mid+y) = d;
            }
      }
      }
      fwrite(pixels, size * size * sizeof(int), 1, out);
      free(pixels); 
      fclose(out); 
      fclose(in);
      return EXIT_SUCCESS;
}
