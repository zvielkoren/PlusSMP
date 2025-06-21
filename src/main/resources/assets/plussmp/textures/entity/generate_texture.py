from PIL import Image, ImageDraw

# Create a new 64x64 image with transparent background
img = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# Color definitions
colors = {
    'purple': (75, 0, 130, 255),    # Main purple
    'gold': (255, 215, 0, 255),     # Gold
    'dark_purple': (48, 0, 82, 255), # Darker purple for shading
    'light_gold': (255, 223, 77, 255) # Lighter gold for highlights
}

def draw_cube_face(x, y, width, height, color, shading=False):
    """Draw a single face of a cube with optional shading"""
    draw.rectangle([(x, y), (x + width - 1, y + height - 1)], fill=color)
    if shading:
        # Add simple shading effect
        shade_color = colors['dark_purple'] if color == colors['purple'] else colors['light_gold']
        draw.rectangle([(x + width - 2, y), (x + width - 1, y + height - 1)], fill=shade_color)
        draw.rectangle([(x, y + height - 2), (x + width - 1, y + height - 1)], fill=shade_color)

# Lower body (12x12x8)
draw_cube_face(0, 0, 12, 12, colors['purple'], True)      # Front
draw_cube_face(12, 0, 8, 12, colors['purple'], True)      # Side
draw_cube_face(20, 0, 12, 12, colors['purple'], True)     # Back

# Middle body (16x12x12)
draw_cube_face(32, 0, 16, 12, colors['purple'], True)     # Front
draw_cube_face(48, 0, 12, 12, colors['purple'], True)     # Side

# Upper body (8x16x8)
draw_cube_face(0, 24, 8, 16, colors['purple'], True)      # Front
draw_cube_face(8, 24, 8, 16, colors['purple'], True)      # Side
draw_cube_face(16, 24, 8, 16, colors['purple'], True)     # Back

# Right arm (4x12x4)
draw_cube_face(32, 24, 4, 12, colors['purple'], True)     # Front
draw_cube_face(36, 24, 4, 12, colors['purple'], True)     # Side
draw_cube_face(40, 24, 4, 12, colors['purple'], True)     # Back

# Left arm (4x12x4)
draw_cube_face(48, 24, 4, 12, colors['purple'], True)     # Front
draw_cube_face(52, 24, 4, 12, colors['purple'], True)     # Side
draw_cube_face(56, 24, 4, 12, colors['purple'], True)     # Back

# Gold belt (12x1x8)
draw_cube_face(0, 48, 12, 1, colors['gold'], True)        # Front
draw_cube_face(12, 48, 8, 1, colors['gold'], True)        # Side
draw_cube_face(20, 48, 12, 1, colors['gold'], True)       # Back

# Gold trim (16x1x12)
draw_cube_face(0, 57, 16, 1, colors['gold'], True)        # Front
draw_cube_face(16, 57, 12, 1, colors['gold'], True)       # Side
draw_cube_face(28, 57, 16, 1, colors['gold'], True)       # Back

def add_gold_trim(x, y, width, height):
    """Add gold trim to edges"""
    # Outer border
    draw.line([(x, y), (x + width - 1, y)], fill=colors['gold'], width=2)                    # Top
    draw.line([(x, y + height - 1), (x + width - 1, y + height - 1)], fill=colors['gold'], width=2)  # Bottom
    draw.line([(x, y), (x, y + height - 1)], fill=colors['gold'], width=2)                   # Left
    draw.line([(x + width - 1, y), (x + width - 1, y + height - 1)], fill=colors['gold'], width=2)   # Right

# Add gold trim to main sections
add_gold_trim(0, 0, 12, 12)     # Lower body front
add_gold_trim(32, 0, 16, 12)    # Middle body front
add_gold_trim(0, 24, 8, 16)     # Upper body front

# Add some texture detail
def add_texture_detail():
    # Add subtle patterns to main body parts
    for x in range(0, 64, 4):
        for y in range(0, 64, 4):
            if img.getpixel((x, y))[:3] == colors['purple'][:3]:  # If it's purple
                draw.point((x, y), colors['dark_purple'])

add_texture_detail()

# Save the image with maximum quality
img.save('plusboss.png', 'PNG', quality=100) 