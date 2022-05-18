#----------------------------------------------------------------
# Generated CMake target import file for configuration "Debug".
#----------------------------------------------------------------

# Commands may need to know the format version.
set(CMAKE_IMPORT_FILE_VERSION 1)

# Import target "MathCalc::MyAdd" for configuration "Debug"
set_property(TARGET MathCalc::MyAdd APPEND PROPERTY IMPORTED_CONFIGURATIONS DEBUG)
set_target_properties(MathCalc::MyAdd PROPERTIES
  IMPORTED_IMPLIB_DEBUG "${_IMPORT_PREFIX}/lib/MyAdd.lib"
  IMPORTED_LOCATION_DEBUG "${_IMPORT_PREFIX}/bin/MyAdd.dll"
  )

list(APPEND _IMPORT_CHECK_TARGETS MathCalc::MyAdd )
list(APPEND _IMPORT_CHECK_FILES_FOR_MathCalc::MyAdd "${_IMPORT_PREFIX}/lib/MyAdd.lib" "${_IMPORT_PREFIX}/bin/MyAdd.dll" )

# Import target "MathCalc::MathCalc" for configuration "Debug"
set_property(TARGET MathCalc::MathCalc APPEND PROPERTY IMPORTED_CONFIGURATIONS DEBUG)
set_target_properties(MathCalc::MathCalc PROPERTIES
  IMPORTED_LOCATION_DEBUG "${_IMPORT_PREFIX}/bin/MathCalc.exe"
  )

list(APPEND _IMPORT_CHECK_TARGETS MathCalc::MathCalc )
list(APPEND _IMPORT_CHECK_FILES_FOR_MathCalc::MathCalc "${_IMPORT_PREFIX}/bin/MathCalc.exe" )

# Commands beyond this point should not need to know the version.
set(CMAKE_IMPORT_FILE_VERSION)
