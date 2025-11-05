import json
import os
from config import SAVE_OUTPUT, OUTPUT_FILE

class OutputHandler:
    def __init__(self):
        self.results = {}

    def add_result(self, key, value):
        """
        Add a result to the output dictionary.
        """
        self.results[key] = value

    def save_to_json(self, file_path=None):
        """
        Save results to a JSON file.
        """
        if not SAVE_OUTPUT:
            return
        if file_path is None:
            file_path = OUTPUT_FILE
        with open(file_path, 'w') as f:
            json.dump(self.results, f, indent=4)
        print(f"Results saved to {file_path}")

    def save_orbital_info(self, mol, mf, file_path="orbitals.txt"):
        """
        Save orbital information to a text file.
        """
        with open(file_path, 'w') as f:
            f.write("Orbital Energies:\n")
            for i, energy in enumerate(mf.mo_energy):
                f.write(f"Orbital {i+1}: {energy:.6f} Hartree\n")
        print(f"Orbital info saved to {file_path}")

    def save_density_cube(self, mol, mf, file_path="density.cube"):
        """
        Save electron density to a cube file.
        """
        from pyscf.tools import cubegen
        cubegen.density(mol, file_path, mf.make_rdm1(), nx=40, ny=40, nz=40)
        print(f"Density cube saved to {file_path}")

    def print_summary(self):
        """
        Print a summary of the results.
        """
        print("\n=== DFT Simulation Results ===")
        for key, value in self.results.items():
            if isinstance(value, float):
                print(f"{key}: {value:.6f}")
            else:
                print(f"{key}: {value}")
        print("============================")
