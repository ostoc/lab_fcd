
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>

#define image(x,y) pixels[((unsigned int)y*size+x)]

/*
 * A geração da imagem, de tamanho 0xB500 só pode ser feita em processadores 64bits,
 * com 8Gb de RAM.
 * Tamanho final da imagem: 8Gb
 * Tempo de geração na om.pcs.ups.br (headnode): 17m
 * Tempo do processamento da imagem:
 */

int main(void) {
	srand(time(NULL ));
	unsigned long long int size;
	unsigned long long int x, y;

	FILE *out;

	out = fopen("image.in", "wb");
	if (out == NULL ) {
		perror("image.in");
		exit(EXIT_FAILURE);
	}

	size = 0x6000;
	printf("%lld\n", size);

	fwrite(&size, sizeof(size), 1, out);
	fflush(out);

	int *pixels = (int*) malloc(size * size * sizeof(int));
	if (pixels == NULL ) {
		perror("malloc pixels");
		exit(EXIT_FAILURE);
	}

	for (y = 0; y < size; y++) {
		for (x = 0; x < size; x++) {
			image(x,y)= (rand()&0xFFFF);
		}
	}
	fwrite(pixels, size * size * sizeof(int), 1, out);

	fflush(out);
	fclose(out);
	free(pixels);

	return EXIT_SUCCESS;
}

