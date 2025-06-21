from PIL import Image, ImageDraw

# Create a new 16x16 image with transparent background
img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# Color definitions
colors = {
    'base': (75, 0, 130, 255),  # Main purple
    'spots': (255, 215, 0, 255),  # Gold
    'outline': (0, 0, 0, 255)  # Black outline
}

# Draw egg shape
# Base shape
draw.ellipse([(1, 1), (15, 15)], fill=colors['base'], outline=colors['outline'])

# Gold spots pattern
spots = [
    (4, 4), (12, 4),
    (3, 8), (13, 8),
    (5, 12), (11, 12)
]
for x, y in spots:
    draw.ellipse([(x-1, y-1), (x+1, y+1)], fill=colors['spots'])

# Save the image
img.save('plus_boss_spawn_egg.png') 