import path from "path";

export const getBuildFoldePath = ((): string => {
  return path.join(__dirname, "../../../app.asar.unpacked/build");
})();
