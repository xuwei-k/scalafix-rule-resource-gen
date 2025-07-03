object Compat {
  def toFile(f: xsbti.HashedVirtualFileRef, converter: xsbti.FileConverter): java.io.File =
    converter.toPath(f).toFile
}
