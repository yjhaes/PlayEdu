# Kurisu look-direction mechanics

Kurisu is a small humanoid anime pet. Keep the feet, lower legs, skirt hem, and lower torso on the same planted baseline across the entire look loop. The gaze leads through the original purple eye construction: redraw each complete eye surface together (white, iris, pupil, eyelid, rim, and highlight) inside its socket, rather than sliding a pupil across a fixed eye. Eyebrows and eyelids make small persona-preserving changes, then the head and neck follow with a restrained near-rigid yaw or pitch. Do not stretch the skull, eyes, mouth, coat, hands, or tie.

Her long auburn hair and tan lab coat are worn, flexible materials. They follow the head and upper torso with a small, continuous lag, while the white shirt, red tie, skirt, tights, and hands stay body-locked. Kurisu has no held prop; the red tie is an attached rigid clothing detail and must remain aligned to the torso. Hair strands may become slightly more or less visible as the head turns, but they must stay attached and must not jump between directions.

Cardinal pose families in viewer/screen coordinates:

- `000` up: face remains broadly frontal, chin and eye line lift subtly, both complete eyes aim toward the top edge, and a little more lower face/neck is visible.
- `090` screen-right: nose tip, pupils, and eye openings move to the screen-right side of the head center; the head turns slightly right, with the opposite cheek/hair edge becoming more occluded.
- `180` down: chin tucks gently, both complete eyes aim toward the bottom edge, bangs cover a little more of the upper eye area, and the upper head/shoulder relationship remains registered.
- `270` screen-left: nose tip, pupils, and eye openings move to the screen-left side of the head center; the head turns slightly left, with the opposite cheek/hair edge becoming more occluded.

Intermediates must be even 22.5-degree steps between these four pose families. Use the same planted lower-body anchor and a roughly equal motion budget for eyes, lids, head/neck, hair, and coat at each step. The sequence must travel clockwise through `000 -> 090 -> 180 -> 270 -> 000`; no whole-sprite rotation, affine tilt, re-centering, mirrored one-off cells, replacement eyes, labels, or detached effects.
