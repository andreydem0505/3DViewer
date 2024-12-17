package com.cgvsu.io.objreader;

import com.cgvsu.io.objreader.exceptions.*;
import com.cgvsu.nmath.Vector2f;
import com.cgvsu.nmath.Vector3f;
import com.cgvsu.model.Group;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ObjReader {

	private static final String OBJ_VERTEX_TOKEN = "v";
	private static final String OBJ_TEXTURE_TOKEN = "vt";
	private static final String OBJ_NORMAL_TOKEN = "vn";
	private static final String OBJ_FACE_TOKEN = "f";
	private static final String OBJ_GROUP_TOKEN = "g";
	private static final String COMMENT_TOKEN = "#";

	private final Model model = new Model();
	protected static boolean force = true;
	private int lineInd = 0;
	private Group currentGroup = null;

	protected ObjReader() {}

	public static Model readFromFilePath(String path) throws IOException {
		Path filename = Path.of(path);
		String fileContent = Files.readString(filename);
		return ObjReader.read(fileContent);
	}

	public static Model read(File file) {
		return read(String.valueOf(file), true);
	}

	public Model read(File file, boolean force) {
		String model;
		try {
			model = Files.readString(Path.of(file.getCanonicalPath()));
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		return read(model, force);
	}

	public static Model read(String model) {
		return read(model, true);
	}

	public static Model read(String model, boolean force) {
		ObjReader objReader = new ObjReader();
		ObjReader.force = force;
		objReader.readModel(model);
		return objReader.model;
	}

	protected void readModel(String fileContent) {
		Locale.setDefault(Locale.ROOT);
		Scanner scanner = new Scanner(fileContent);
		while (scanner.hasNextLine()) {
			String line = deleteCommentLine(scanner.nextLine().trim());

			if (line.isBlank()) {
				continue;
			}

			ArrayList<String> wordsInLine = new ArrayList<>(Arrays.asList(line.split("\\s+")));
			if (wordsInLine.isEmpty()) {
				continue;
			}

			final String token = wordsInLine.get(0);
			wordsInLine.remove(0);

			++lineInd;
			switch (token) {
				case OBJ_VERTEX_TOKEN -> model.vertices.add(parseVertex(wordsInLine, lineInd));
				case OBJ_TEXTURE_TOKEN -> model.textureVertices.add(parseTextureVertex(wordsInLine, lineInd));
				case OBJ_NORMAL_TOKEN -> model.normals.add(parseNormal(wordsInLine, lineInd));
				case OBJ_FACE_TOKEN -> handleFace(wordsInLine);
				case OBJ_GROUP_TOKEN -> handleGroup(wordsInLine);
				default -> {
					if (!force) {
						throw new TokenException(lineInd);
					}
				}
			}
		}

		if (currentGroup != null) {
			model.addGroup(currentGroup);
		}

		int verticesSize = model.getVerticesSize();
		int textureVerticesSize = model.getTextureVerticesSize();
		int normalsSize = model.getNormalsSize();
		for (Polygon polygon: model.getPolygons()) {
			polygon.checkIndices(verticesSize, textureVerticesSize, normalsSize);
		}
	}

	private static String deleteCommentLine(String line) {
		//удаляю комментарий с #
		int commentIndex = line.indexOf(COMMENT_TOKEN);
		if (commentIndex > -1) {
			line = line.substring(0, commentIndex);
		}
		return line;
	}

	protected Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken, int index) throws ObjReaderException {
		checkForErrors(wordsInLineWithoutToken, index, 3);
		try {
			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));

		} catch (ParsingException e) {
			throw new ParsingException("float", index);
		}
	}

	protected Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken, int index) {
		checkForErrors(wordsInLineWithoutToken, index, 2);
		try {
			return new Vector2f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)));

		} catch (ParsingException e) {
			throw new ParsingException("float", index);
		}
	}

	protected Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken, int index) {
		checkForErrors(wordsInLineWithoutToken, index, 3);
		try {
			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));

		} catch (ParsingException e) {
			throw new ParsingException("float", index);
		}
	}

	protected Polygon parseFace(final ArrayList<String> wordsInLineWithoutToken, int index) {
		List<FaceWord> faceWords = new ArrayList<>();
		Set<WordType> types = new HashSet<>();

		for (String word : wordsInLineWithoutToken) {
			FaceWord faceWord = FaceWord.parse(word, index, force);
			types.add(faceWord.getWordType());
			faceWords.add(faceWord);
		}

		if (faceWords.size() < 3) {
			throw new ArgumentsException(ArgumentsErrorType.FEW_IN_POLYGON, index);
		}
		if (types.size() > 1) {
			throw new FaceTypeException(index);
		}

		return createPolygon(faceWords.toArray(new FaceWord[0]));
	}

	private void handleFace(final ArrayList<String> wordsInLineWithoutToken) {
		Polygon polygon = parseFace(wordsInLineWithoutToken, lineInd);

		if (!model.getPolygons().isEmpty()) {
			Polygon firstPolygon = model.getFirstPolygon();
			if (polygon.hasTexture() != firstPolygon.hasTexture()) {
				throw new TextureException(lineInd);
			}
		}

		model.addPolygon(polygon);
		if (currentGroup != null) {
			currentGroup.addPolygon(polygon);
		}
	}

	private void handleGroup(final ArrayList<String> wordsInLineWithoutToken) {
		if (wordsInLineWithoutToken.isEmpty()) {
			throw new GroupNameException(lineInd);
		}

		if (currentGroup != null) {
			model.addGroup(currentGroup);
		}

		StringBuilder sb = new StringBuilder();
		for (String s : wordsInLineWithoutToken) {
			sb.append(s).append(' ');
		}
		sb.deleteCharAt(sb.length() - 1);
		currentGroup = new Group(sb.toString());
	}

	protected Polygon createPolygon(FaceWord[] faceWords) {
		Polygon polygon = new Polygon();
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		ArrayList<Integer> textureVertexIndices = new ArrayList<>();
		ArrayList<Integer> normalIndices = new ArrayList<>();

		for (FaceWord faceWord : faceWords) {
			Integer vertexIndex = faceWord.getVertexIndex();
			if (vertexIndex != null) {
				vertexIndices.add(vertexIndex);
			}
			Integer textureVertexIndex = faceWord.getTextureVertexIndex();
			if (textureVertexIndex != null) {
				textureVertexIndices.add(textureVertexIndex);
			}
			Integer normalIndex = faceWord.getNormalIndex();
			if (normalIndex != null) {
				normalIndices.add(normalIndex);
			}
		}

		polygon.setVertexIndices(vertexIndices);
		polygon.setTextureVertexIndices(textureVertexIndices);
		polygon.setNormalIndices(normalIndices);
		polygon.setLineIndex(lineInd);

		return polygon;
	}

	private void checkForErrors(final ArrayList<String> wordsInLineWithoutToken, int index, int size) {
		if (!force && wordsInLineWithoutToken == null) {
			throw new ArgumentsException(ArgumentsErrorType.NULL, index);
		}
		checkSize(wordsInLineWithoutToken.size(), size, index);
	}

	private void checkSize(int wordCount, int vectorSize, int index) {
		if (wordCount == vectorSize) {
			return;
		}
		if (wordCount < vectorSize) {
			throw new ArgumentsException(ArgumentsErrorType.FEW, index);
		}
		if (!force) {
			throw new ArgumentsException(ArgumentsErrorType.MANY, index);
		}
	}
}