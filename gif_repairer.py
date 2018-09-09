import os
from PIL import Image, ImageSequence





def repair(path):
	im = Image.open(path)


	if is_partial(im):
		print('REPAIRING:!', path)
		frames = ImageSequence.Iterator(im)
		def repaired_frames(frames):
			last_frame = im

			for frame in frames:
				new_frame = frame
				new_frame.paste(last_frame)
				last_frame = new_frame
				yield new_frame

		frames = repaired_frames(frames)

		output_image = next(frames)
		output_image.info = im.info
		output_image.save(path, save_all=True, append_images=list(frames))


def is_partial(im):
    try:
        while True:
            if im.tile:
                tile = im.tile[0]
                update_region = tile[1]
                update_region_dimensions = update_region[2:]
                if update_region_dimensions != im.size:
                    return True
            im.seek(im.tell() + 1)
    except EOFError:
        return False
    return False

if __name__ == "__main__":
	for file in os.listdir('./'):
		if (file.endswith(".gif")):
			repair(file)