from pathlib import Path
import difflib
import subprocess

root = Path.cwd()
scratch = root / '.scratch/points-structure-cleanup'
names = {'PointBalanceChange': 'types', 'PointBalanceChangeResult': 'types', 'PointBalanceException': 'exception'}
files = (scratch / 'affected-files.txt').read_text().splitlines()
patch = []
for name in files:
    path = root / name
    before = (scratch / 'before' / name).read_text(encoding='utf-8')
    after = before
    for symbol, package in names.items():
        after = after.replace(f'xyz.playedu.points.service.{symbol}', f'xyz.playedu.points.{package}.{symbol}')
    dest = path
    if path.stem in names:
        package = names[path.stem]
        after = after.replace('package xyz.playedu.points.service;', f'package xyz.playedu.points.{package};')
        dest = path.parent.parent / package / path.name
        dest.parent.mkdir(exist_ok=True)
    elif path.name == 'PointBalanceService.java':
        after = after.replace('/** The only', 'import xyz.playedu.points.types.PointBalanceChange;\nimport xyz.playedu.points.types.PointBalanceChangeResult;\n\n/** The only')
    elif path.name == 'PointBalanceServiceIntegrationTest.java':
        after = after.replace('import xyz.playedu.points.domain.PointLedgerType;', 'import xyz.playedu.points.domain.PointLedgerType;\nimport xyz.playedu.points.exception.PointBalanceException;\nimport xyz.playedu.points.types.PointBalanceChange;\nimport xyz.playedu.points.types.PointBalanceChangeResult;')
    lines = after.splitlines(keepends=True)
    indexes = [i for i, line in enumerate(lines) if line.startswith('import ') and not line.startswith('import static ')]
    for i, line in zip(indexes, sorted(lines[i] for i in indexes)):
        lines[i] = line
    after = ''.join(lines)
    dest.write_bytes(after.replace('\n', '\r\n').encode('utf-8'))
    if dest != path:
        path.unlink(missing_ok=True)
        subprocess.run(['git', 'add', '--', str(path.relative_to(root)), str(dest.relative_to(root))], check=True)
    else:
        patch.extend(difflib.unified_diff(before.splitlines(keepends=True), after.splitlines(keepends=True), fromfile='a/' + path.relative_to(root).as_posix(), tofile='b/' + path.relative_to(root).as_posix()))
(scratch / 'relocation.patch').write_text(''.join(patch), encoding='utf-8')
subprocess.run(['git', 'apply', '--cached', str(scratch / 'relocation.patch')], check=True)
