# Denia look mechanics

## Natural motion

Denia is a humanoid anime sprite with a stable lower body, a detailed face, long soft hair, and large worn ribbons. Keep her feet, boots, skirt hem, and lower torso planted as the registration anchor. The gaze leads with the original anime eyes: preserve the existing eye apertures and move the irises/pupils and eyelids as drawn facial features, with small eyebrow and mouth changes only when needed. The head and neck follow the eyes with a restrained turn or pitch, while the shoulders and upper torso follow only slightly. Never rotate, skew, or tilt the whole sprite.

Her long pink-lavender hair and cape-like ribbon tails are flexible and should lag the head by a small amount, maintaining their recognizable arcs and volume. The large bow, blue gem hair ornament, gloves, dress, and boots are worn or attached: they follow the head or body naturally, may become partly occluded during a turn, and must not teleport or change design. The lower-body anchor remains stable while the upper silhouette bends subtly toward the target.

## Cardinal pose families

- `000` up: keep the torso and lower body broadly frontal; lift the eyes, upper eyelids, and chin toward the top edge. The fringe and upper head angle follow upward, while the bow and hair tails remain anchored and settle slightly below the head.
- `090` screen-right: turn the eyes and face toward the viewer's right, with the nose/face center and visible cheek shifting right of the head center. The right-facing side of the hair and bow becomes more visible; the opposite cheek, eye edge, and rear ribbon are partly occluded. Shoulders follow only a little.
- `180` down: keep the boots and lower torso planted; lower the eyes and face toward the bottom edge with a small chin tuck and lowered upper head angle. Hair and bow follow downward without collapsing the head or changing the costume proportions.
- `270` screen-left: mirror the physical sense of the rightward turn in screen coordinates: face, eyes, and nose/face center shift left of the head center. The left-facing hair and bow side becomes more visible, with the opposite cheek, eye edge, and rear ribbon partly occluded. Preserve the same lower-body anchor and restrained shoulder follow-through.

## Interpolation and motion budget

Use one continuous clockwise arc: `000 -> 090 -> 180 -> 270 -> 000`. Each 22.5-degree step should move the eyes/eyelids, head angle, upper torso, and hair/ribbon follow-through by roughly the same visual amount. Keep scale, feet, lower torso, baseline, palette, line quality, eye construction, and costume details stable. The intermediate diagonals blend the adjacent cardinal pose families; no single neighbor may introduce a larger bend, prop shift, occlusion jump, or silhouette change without a physical transition. The row boundary `157.5 -> 180` and `337.5 -> 000` must be smooth.

At 192x208 display size, cardinal landmarks must be unmistakable: eyes and face point up at `000`, screen-right at `090`, down at `180`, and screen-left at `270`. Intermediate directions may use subtler eye and hair cues, but they must remain in the intended quadrant and never reverse. Use the original eyes only; do not add replacement googly eyes, labels, arrows, shadows, glow, or detached effects.
