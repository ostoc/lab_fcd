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
      fread(&size, sizeof(size), 1, in);  //从第二个数据开始读取， 不改变开头的文件大小
      fwrite(&size, sizeof(size), 1, out);      //从第二个数据开始写入
      int *pixels = (int *) malloc(size * size * sizeof(int)); //分配 pixel存储空间
      if (!fread(pixels, size * size * sizeof(int), 1, in)) { //判断分配空间和读取的空间是否相同
      perror("read error");
      exit(EXIT_FAILURE);
      }
      // todo: 需要定义长宽大小

// haar // 变化先做完所有的行变换然后在做列变换，
      SQRT_2 = sqrt(4);
      for (s = size; s > 1; s /= 2) {
            // 变换时通过两次居中对切变换 s / 2 
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
